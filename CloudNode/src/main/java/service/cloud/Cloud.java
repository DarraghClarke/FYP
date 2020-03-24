package service.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import service.cloud.transferServices.TransferServer;
import service.core.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Cloud extends WebSocketClient {

    private File service;
    private UUID assignedUUID;
    private Proxy proxyName;
    SystemInfo nodeSystem = new SystemInfo();
    private Map<Integer,Double> historicalCPUload = new HashMap<>();

    public Cloud(URI serverUri, File service) {
        super(serverUri);
        this.service = service;//service is stored in edge node
        //this.proxyName = proxyName;
        System.out.println(serverUri+" space   "+service.getAbsolutePath());
        getCPULoad();
    }

    public static void main(String[] args) throws URISyntaxException {
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String message) {
        RuntimeTypeAdapterFactory<Message> adapter = RuntimeTypeAdapterFactory
                .of(Message.class, "type")
                .registerSubtype(NodeInfo.class, Message.MessageTypes.NODE_INFO)
                .registerSubtype(Service.class, Message.MessageTypes.SERVICE)
                .registerSubtype(ServerHeartbeatRequest.class, Message.MessageTypes.SERVER_HEARTBEAT_REQUEST)
                .registerSubtype(ServiceRequest.class, Message.MessageTypes.SERVICE_REQUEST)
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
                InetSocketAddress serverAddress = launchTempServer();
                ServiceResponse serviceResponse = new ServiceResponse(serviceRequest.getRequstorID(), assignedUUID, serverAddress.getHostName() + ":" + serverAddress.getPort());
                System.out.println(serviceResponse.getServiceOwnerAddress());
                String jsonStr = gson.toJson(serviceResponse);
                System.out.println(jsonStr);
                send(jsonStr);
                break;
        }
    }
    public void sendHeartbeatResponse(){
        Gson gson = new Gson();
        NodeInfo nodeInfo = new NodeInfo(assignedUUID, null, service.getName());
        if (!historicalCPUload.isEmpty()){
            nodeInfo.setCPUload(historicalCPUload);
        }
        System.out.println(service.getName());
        String jsonStr = gson.toJson(nodeInfo);
        send(jsonStr);
    }

    public void getCPULoad(){
        HardwareAbstractionLayer hal = nodeSystem.getHardware();
        CentralProcessor processor = hal.getProcessor();
        new Timer().schedule(
                new TimerTask() {
                    int secondcounter=0;
                    @Override
                    public void run() {
                        secondcounter++;
                        historicalCPUload.put(secondcounter,processor.getSystemCpuLoadBetweenTicks() * 100);
                    }
                }, 0, 1000);

    }

    public InetSocketAddress launchTempServer() {
        InetSocketAddress serverAddress = new InetSocketAddress(6969);
        System.out.println(serverAddress.toString());
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
}
