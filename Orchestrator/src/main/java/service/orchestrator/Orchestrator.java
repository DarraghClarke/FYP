package service.orchestrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import service.core.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Orchestrator extends WebSocketServer {

    private Map<UUID, NodeInfo> connectedNodes = new HashMap<>();
    UUID firstid;

    public Orchestrator(int port) {
        super(new InetSocketAddress(port));
        System.out.println(this.getAddress().toString());
    }

    public static void main(String[] args) {
        System.out.println("we out here");
    }

    /**
     * this method is called whenever a new connection is made with the orchestrator,
     * the orchestrator will then send a NodeInfoRequest back to the connection along with a UUID used for future communications
     *
     * @param webSocket
     * @param clientHandshake
     */
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        System.out.println("new connection");
        System.out.println(webSocket.getRemoteSocketAddress());
        //todo add real checking logic here (update 12/02/2020 this may not be neccessary)
        UUID UUIDToReturn = UUID.randomUUID();
        if (connectedNodes.isEmpty()) {
            firstid = UUIDToReturn;
        }
        connectedNodes.put(UUIDToReturn, null);//no node information yet to add


        //create a nodeInfoRequest and send it back to the node
        Gson gson = new Gson();
        NodeInfoRequest infoRequest = new NodeInfoRequest(UUIDToReturn);
        String jsonStr = gson.toJson(infoRequest);
        webSocket.send(jsonStr);
//        new Timer().schedule(
//                new TimerTask() {
//
//                    @Override
//                    public void run() {
//
//                        //todo make this a "while nodes connected
//                        //create a nodeInfoRequest and send it back to the node
//                        Gson gson = new Gson();
//                        NodeInfoRequest infoRequest = new NodeInfoRequest(UUIDToReturn);
//                        String jsonStr = gson.toJson(infoRequest);
//                        webSocket.send(jsonStr);
//
//                        while(connectedNodes.size()>=2){//as long as we have 2 nodes we can check if its time to swap
//
//                        }
//                    }
//                }, 0, 20000);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        RuntimeTypeAdapterFactory<Message> adapter = RuntimeTypeAdapterFactory
                .of(Message.class, "type")
                .registerSubtype(NodeInfo.class, Message.MessageTypes.NODE_INFO)
                .registerSubtype(ServiceRequest.class, Message.MessageTypes.SERVICE_REQUEST)
                .registerSubtype(ServiceResponse.class, Message.MessageTypes.SERVICE_RESPONSE)
                .registerSubtype(NodeInfoRequest.class, Message.MessageTypes.NODE_INFO_REQUEST);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        Message messageObj = gson.fromJson(message, Message.class);

        System.out.println(messageObj.getType());
        System.out.println(message);

        //this routes inbound messages based on type and then moves them to other methods
        switch (messageObj.getType()) {
            case Message.MessageTypes.NODE_INFO:
                NodeInfo nodeInfo = (NodeInfo) messageObj;
                nodeInfo.setWebSocket(webSocket);
                connectedNodes.put(nodeInfo.getSystemID(), nodeInfo);
                break;
            case Message.MessageTypes.SERVICE_REQUEST:
                ServiceRequest serviceRequest = (ServiceRequest) messageObj;
                String jsonStr = gson.toJson(serviceRequest);
                WebSocket a = findServiceOwnerAddress(serviceRequest);//todo null proof this
                a.send(jsonStr);
                break;
            case Message.MessageTypes.SERVICE_RESPONSE:
                ServiceResponse response = (ServiceResponse) messageObj;
                WebSocket returnSocket = connectedNodes.get(response.getRequstorID()).getWebSocket();
                System.out.println(response.getServiceOwnerAddress());
                jsonStr = gson.toJson(response);
                returnSocket.send(jsonStr);
                break;
        }
    }

    public WebSocket findServiceOwnerAddress(ServiceRequest serviceRequest) {
        for (Map.Entry<UUID, NodeInfo> entry : connectedNodes.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().getServiceName());
            if (entry.getValue().getServiceName() != null && entry.getValue().getServiceName().equals(serviceRequest.getServiceName())) {
                System.out.println("here" + entry.getValue().getWebSocket());
                return entry.getValue().getWebSocket();
            }
        }
        return null;

    }


    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
