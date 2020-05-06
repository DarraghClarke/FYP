package service.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import service.core.*;
import service.host.ServiceHost;
import service.transfer.DockerController;
import service.transfer.TransferClient;
import service.transfer.TransferServer;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

public class Cloud extends WebSocketClient {

    private File service;
    private UUID assignedUUID;
    private URI serviceAddress;
    SystemInfo nodeSystem = new SystemInfo();
    HardwareAbstractionLayer hal = nodeSystem.getHardware();
    CentralProcessor processor = hal.getProcessor();
    GlobalMemory memory = hal.getMemory();
    DockerController dockerController;
    private Map<Integer, Double> historicalCPUload = new HashMap<>();
    private Map<Integer, Double> historicalRamload = new HashMap<>();
    boolean secureMode;

    public Cloud(URI serverUri, File service, URI serviceAddress, Boolean secureMode) {
        super(serverUri);
        this.service = service;//service is stored in edge node
        dockerController = new DockerController();
        this.serviceAddress = serviceAddress;
        this.secureMode = secureMode;
        getCPULoad();
        getRamLoad();
    }

    @Override
    public void onMessage(String message) {
        RuntimeTypeAdapterFactory<Message> adapter = RuntimeTypeAdapterFactory
                .of(Message.class, "type")
                .registerSubtype(NodeInfo.class, Message.MessageTypes.NODE_INFO)
                .registerSubtype(Service.class, Message.MessageTypes.SERVICE)
                .registerSubtype(ServerHeartbeatRequest.class, Message.MessageTypes.SERVER_HEARTBEAT_REQUEST)
                .registerSubtype(ServiceRequest.class, Message.MessageTypes.SERVICE_REQUEST)
                .registerSubtype(ServiceResponse.class, Message.MessageTypes.SERVICE_RESPONSE)
                .registerSubtype(NodeInfoRequest.class, Message.MessageTypes.NODE_INFO_REQUEST);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        Message messageObj = gson.fromJson(message, Message.class);

        //this routes inbound messages based on type and then moves them to other methods
        switch (messageObj.getType()) {
            //A request for the nodes status when it initially joins
            case Message.MessageTypes.NODE_INFO_REQUEST:
                NodeInfoRequest infoRequest = (NodeInfoRequest) messageObj;
                assignedUUID = infoRequest.getAssignedUUID();
                sendHeartbeatResponse();
                break;
            //heartbeat request
            case Message.MessageTypes.SERVER_HEARTBEAT_REQUEST:
                sendHeartbeatResponse();
                break;
            //request for the service on the node
            case Message.MessageTypes.SERVICE_REQUEST:
                System.out.println("Time that Cloud gets the request from the Orchestrator " + System.currentTimeMillis());
                ServiceRequest serviceRequest = (ServiceRequest) messageObj;
                gson = new Gson();
                launchTempServer();
                ServiceResponse serviceResponse = new ServiceResponse(serviceRequest.getRequstorID(), assignedUUID, serviceAddress.getHost() + ":" + serviceAddress.getPort());
                String jsonStr = gson.toJson(serviceResponse);
                send(jsonStr);
                break;
            case Message.MessageTypes.SERVICE_RESPONSE:
                //this gives the proxy address we want
                ServiceResponse response = (ServiceResponse) messageObj;
                System.out.println(response);
                System.out.println(response.getServiceOwnerAddress());

                try {
                    launchTransferClient(response.getServiceOwnerAddress());

                } catch (URISyntaxException | UnknownHostException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void sendHeartbeatResponse() {
        Gson gson = new Gson();
        NodeInfo nodeInfo = new NodeInfo(assignedUUID, null, service.getName());
        nodeInfo.setServiceHostAddress(serviceAddress);
        if (!historicalCPUload.isEmpty()) {
            nodeInfo.setCPUload(historicalCPUload);
        }
        if (!historicalRamload.isEmpty()) {
            nodeInfo.setRamLoad(historicalRamload);
        }
        System.out.println(service.getName());
        String jsonStr = gson.toJson(nodeInfo);
        send(jsonStr);
    }

    public void launchTransferClient(String serverAddress) throws URISyntaxException, UnknownHostException {
        URI transferServerURI;
        if (secureMode) {
            transferServerURI = new URI("wss://" + serverAddress);
        } else {
            transferServerURI = new URI("ws://" + serverAddress);
        }
        TransferClient transferClient = new TransferClient(transferServerURI, dockerController);
        transferClient.connect();
        while (transferClient.dockerControllerReady() == null) {
        }
        DockerController dockerController = transferClient.dockerControllerReady();
        transferClient.close();
        ServiceHost serviceHost = new ServiceHost(serviceAddress.getPort(), dockerController);
        serviceHost.run();
    }


    public void getCPULoad() {
        new Timer().schedule(
                new TimerTask() {
                    int secondCounter = 0;

                    @Override
                    public void run() {
                        secondCounter++;
                        historicalCPUload.put(secondCounter, processor.getSystemCpuLoadBetweenTicks() * 100);
                    }
                }, 0, 1000);
    }

    public void getRamLoad() {
        new Timer().schedule(
                new TimerTask() {
                    int secondCounter = 0;

                    @Override
                    public void run() {
                        secondCounter++;
                        historicalRamload.put(secondCounter, (double) ((memory.getAvailable() / memory.getTotal()) * 100));
                    }
                }, 0, 1000);
    }

    public InetSocketAddress launchTempServer() {
        InetSocketAddress serverAddress = new InetSocketAddress(serviceAddress.getPort());
        setReuseAddr(true);
        TransferServer transferServer = new TransferServer(serverAddress, service);
        transferServer.start();

        return serverAddress;
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }
}
