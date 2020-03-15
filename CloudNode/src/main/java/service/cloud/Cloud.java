package service.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import oshi.SystemInfo;
import service.cloud.transferServices.TransferServer;
import service.core.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.UUID;

public class Cloud extends WebSocketClient {

    private File service;
    private UUID assignedUUID;
    private Proxy proxyName;
    SystemInfo nodeSystem = new SystemInfo();

    public Cloud(URI serverUri, File service, Proxy proxyName) {
        super(serverUri);
        this.service = service;//service is stored in edge node
        this.proxyName = proxyName;

    }

    public static void main(String[] args) {
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
                gson = new Gson();
                NodeInfo nodeInfo = new NodeInfo(assignedUUID, nodeSystem, service.getName());
                System.out.println(service.getName());
                String jsonStr = gson.toJson(nodeInfo);
                send(jsonStr);
                break;
            case Message.MessageTypes.SERVICE_REQUEST:
                ServiceRequest serviceRequest = (ServiceRequest) messageObj;
                gson = new Gson();
                InetSocketAddress serverAddress = launchTempServer();
                ServiceResponse serviceResponse = new ServiceResponse(serviceRequest.getRequstorID(), assignedUUID, serverAddress.getHostName() + ":" + serverAddress.getPort());
                System.out.println(serviceResponse.getServiceOwnerAddress());
                jsonStr = gson.toJson(serviceResponse);
                System.out.println(jsonStr);
                send(jsonStr);
                break;
        }
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
