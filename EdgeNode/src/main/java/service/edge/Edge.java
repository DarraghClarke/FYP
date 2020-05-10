package service.edge;

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
import service.host.*;
import service.transfer.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

public class Edge extends WebSocketClient {
    SystemInfo nodeSystem = new SystemInfo();
    HardwareAbstractionLayer hal = nodeSystem.getHardware();
    DockerController dockerController;
    private boolean trustWorthyNode, secureMode;
    private URI serviceAddress;
    private File service;
    private UUID assignedUUID;
    private Map<Integer, Double> historicalCPUload = new HashMap<>();
    private Map<Integer, Double> historicalRamload = new HashMap<>();

    public Edge(URI serverUri, boolean trustWorthy, URI serviceAddress, boolean secure) {
        super(serverUri);
        dockerController = new DockerController();
        this.serviceAddress = serviceAddress;
        trustWorthyNode = trustWorthy;
        secureMode = secure;
        getSystemLoad();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("connected to orchestrator");
        System.out.println(Edge.this.getLocalSocketAddress());//this is the local address in theory
    }

    /**
     * When the websocket library receives any messages they are routed to this method
     *
     * @param message   the message received
     */
    @Override
    public void onMessage(String message) {
        RuntimeTypeAdapterFactory<Message> adapter = RuntimeTypeAdapterFactory
                .of(Message.class, "type")
                .registerSubtype(NodeInfo.class, Message.MessageTypes.NODE_INFO)
                .registerSubtype(Service.class, Message.MessageTypes.SERVICE)
                .registerSubtype(ServiceRequest.class, Message.MessageTypes.SERVICE_REQUEST)
                .registerSubtype(ServerHeartbeatRequest.class, Message.MessageTypes.SERVER_HEARTBEAT_REQUEST)
                .registerSubtype(ServiceResponse.class, Message.MessageTypes.SERVICE_RESPONSE)
                .registerSubtype(NodeInfoRequest.class, Message.MessageTypes.NODE_INFO_REQUEST);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        Message messageObj = gson.fromJson(message, Message.class);

        System.out.println(messageObj.getType());
        System.out.println(message);

        //this routes inbound messages based on type and then moves them to other methods
        switch (messageObj.getType()) {
            case Message.MessageTypes.NODE_INFO_REQUEST:
                NodeInfoRequest infoRequest = (NodeInfoRequest) messageObj;
                assignedUUID = infoRequest.getAssignedUUID();
                sendHeartbeatResponse();
                break;
            case Message.MessageTypes.SERVER_HEARTBEAT_REQUEST:
                sendHeartbeatResponse();
                break;
            case Message.MessageTypes.SERVICE_REQUEST:
                ServiceRequest serviceRequest = (ServiceRequest) messageObj;
                gson = new Gson();
                try {
                    InetSocketAddress serverAddress = launchTransferServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ServiceResponse serviceResponse = new ServiceResponse(serviceRequest.getRequstorID(), assignedUUID, serviceAddress.getHost() + ":" + serviceAddress.getPort(),serviceRequest.getServiceName());
                String jsonStr = gson.toJson(serviceResponse);
                send(jsonStr);
                break;
            case Message.MessageTypes.SERVICE_RESPONSE:
                //this gives the proxy address we want
                ServiceResponse response = (ServiceResponse) messageObj;
                System.out.println("Time that EDge gets the Response from the Orchestrator " + System.currentTimeMillis());

                try {
                    launchTransferClient(response.getServiceOwnerAddress());
                    MigrationSuccess migrationSuccess = new MigrationSuccess(assignedUUID,response.getServiceOwnerID(),response.getServiceName());
                    jsonStr = gson.toJson(migrationSuccess);
                    send(jsonStr);
                } catch (URISyntaxException | UnknownHostException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * Constructs and sends Heartbeat responses when called
     */
    private void sendHeartbeatResponse() {
        Gson gson = new Gson();
        NodeInfo nodeInfo = new NodeInfo(assignedUUID, null, null);
        nodeInfo.setServiceHostAddress(serviceAddress);
        nodeInfo.setTrustyworthy(trustWorthyNode);
        if (!historicalCPUload.isEmpty()) {
            nodeInfo.setCPUload(historicalCPUload);
        }
        if (!historicalRamload.isEmpty()) {
            nodeInfo.setRamLoad(historicalRamload);
        }
        String jsonStr = gson.toJson(nodeInfo);
        send(jsonStr);
    }

    /**
     * This method launches this nodes Transfer Server using the service address define at node creation
     *
     * @return the InetSocketAddress of the new temp server
     */
    private InetSocketAddress launchTransferServer() throws Exception {
        InetSocketAddress serverAddress = new InetSocketAddress(serviceAddress.getPort());
        setReuseAddr(true);
        if (!secureMode) {
            TransferServer transferServer = new TransferServer(serverAddress, service);
            transferServer.start();
        } else {
            new SecureTransferServer(serverAddress, service);
        }

        return serverAddress;
    }

    /**
     * This method creates and launches the TransferClient for this client,
     * If this node is in secure mode then the TransferClient will also be in secure mode
     * After a successful connection this method will start the launch process for the new service.
     *
     * @param serverAddress The address of the TransferServer that is trying to be connected to
     * @throws URISyntaxException
     * @throws UnknownHostException
     */
    private void launchTransferClient(String serverAddress) throws URISyntaxException, UnknownHostException {
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
        launchServiceOnDockerController(dockerController);
    }

    /**
     * This method will launch the host server that will allow users to communicate with the docker instance
     *
     * @param dockerController takes in the dockerController which has the service information
     * @throws UnknownHostException
     */
    private void launchServiceOnDockerController(DockerController dockerController) throws UnknownHostException {
        String[] array = serviceAddress.toString().split(":");
        ServiceHost serviceHost = new ServiceHost(Integer.parseInt(array[5]), dockerController);
        serviceHost.run();
    }

    /**
     * This method polls the system every second and stores pecentage values for CPU and Ram Usage
     */
    private void getSystemLoad() {
        new Timer().schedule(
                new TimerTask() {
                    int secondCounter = 0;

                    @Override
                    public void run() {
                        secondCounter++;
                        historicalCPUload.put(secondCounter, hal.getProcessor().getSystemCpuLoadBetweenTicks() * 100);
                        historicalRamload.put(secondCounter, (double) ((hal.getMemory().getAvailable() / hal.getMemory().getTotal()) * 100));
                    }
                }, 0, 1000);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
