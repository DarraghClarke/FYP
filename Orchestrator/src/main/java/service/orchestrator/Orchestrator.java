package service.orchestrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import service.core.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;

public class Orchestrator extends WebSocketServer {

    private Map<UUID, NodeInfo> connectedNodes = new HashMap<>();
    UUID firstid;

    public Orchestrator(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        System.out.println(InetAddress.getLocalHost().getHostAddress().trim());
        System.out.println(this.getAddress().toString());
    }

    public static void main(String[] args) throws UnknownHostException {
        Orchestrator orchestrator = new Orchestrator(443);
        orchestrator.run();
        System.out.println("not encrypted version");
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

        new Timer().schedule(
                new TimerTask() {

                    @Override
                    public void run() {

                        //todo make this a "while nodes connected
                        //create a ServerHeartbeatRequest and send it back to the node
                        Gson gson = new Gson();
                        ServerHeartbeatRequest heartbeat = new ServerHeartbeatRequest(UUIDToReturn);
                        String jsonStr = gson.toJson(heartbeat);
                        webSocket.send(jsonStr);

                    }
                }, 20000, 20000);
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
                System.out.println("ADDED THIS " + serviceRequest.toString());
                WebSocket a = findBestServiceOwnerAddress(serviceRequest).getWebSocket();//todo null proof this
                a.send(jsonStr);
                break;
            case Message.MessageTypes.SERVICE_RESPONSE:
                ServiceResponse response = (ServiceResponse) messageObj;
                WebSocket returnSocket = connectedNodes.get(response.getRequstorID()).getWebSocket();
                System.out.println(response.getServiceOwnerAddress());
                jsonStr = gson.toJson(response);
                returnSocket.send(jsonStr);
                break;
            case Message.MessageTypes.HOST_REQUEST:
                HostRequest hostRequest = (HostRequest) messageObj;
                long startTime = System.nanoTime();
                ServiceRequest requestFromUser = new ServiceRequest(hostRequest.getRequestorID(),hostRequest.getRequestedServiceName());
                NodeInfo returnedNode = findBestServiceOwnerAddress(requestFromUser);
                URI returnURI = returnedNode.getServiceHostAddress();

                jsonStr = gson.toJson(new HostResponse(hostRequest.getRequestorID(),returnURI));
                webSocket.send(jsonStr);//this sends to requestor address
                long endTime = System.nanoTime();
                break;
        }
    }

    public NodeInfo findBestServiceOwnerAddress(ServiceRequest serviceRequest) {
        Map<UUID, NodeInfo> allServiceOwnerAddresses = findAllServiceOwnerAddresses(serviceRequest);
        NodeInfo bestNode = null;
        double bestNodeScore = 200;
        if (allServiceOwnerAddresses == null) {
            //noone had this service, inform edgenode
        }
        //else if(allServiceOwnerAddresses.size()==1){ //case wheres there only one entry
        //    bestNodeSocket=allServiceOwnerAddresses.;
        //}
        else {
            for (Map.Entry<UUID, NodeInfo> entry : allServiceOwnerAddresses.entrySet()) {
                //Map<Integer, Double> entryCPULoad = entry.getValue().getCPUload();
                double currentNodeCPUScore = calculateRecentCPULoad(entry.getValue().getCPUload(),entry.getValue().getRollingCPUScore());
                double currentNodeRamScore = calculateRecentRamLoad(entry.getValue().getRamLoad(),entry.getValue().getRollingRamScore());
                if (currentNodeCPUScore+currentNodeRamScore < bestNodeScore && entry.getValue().isTrustyworthy()) {
                    bestNodeScore = currentNodeCPUScore;
                    bestNode = entry.getValue();
                }
            }
        }
        return bestNode;
    }

    public double calculateRecentCPULoad(Map<Integer, Double> entryCPULoad,double rollingScore) {
        double runningTotal = -1;
        if (entryCPULoad.size() >= 5) {
            for (int i = entryCPULoad.size() - 1; i > entryCPULoad.size() - 5; i--) {
                runningTotal = +entryCPULoad.get(i);
            }
        }

        return (0.2*rollingScore)+(0.8*(runningTotal / 5));
    }

    public double calculateRecentRamLoad(Map<Integer, Double> entryRamLoad,double rollingScore) {
        double runningTotal=-1;
        if (entryRamLoad.size() >= 5) {
            for (int i = entryRamLoad.size() - 1; i > entryRamLoad.size() - 5; i--) {
                runningTotal = + entryRamLoad.get(i);
            }
        }
        return (0.2*rollingScore)+(0.8*(runningTotal / 5));
    }


    public Map<UUID, NodeInfo> findAllServiceOwnerAddresses(ServiceRequest serviceRequest) {
        Map<UUID, NodeInfo> toReturn = new HashMap<>();
        for (Map.Entry<UUID, NodeInfo> entry : connectedNodes.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().getServiceName());
            if (entry.getValue().getServiceName() != null && entry.getValue().getServiceName().equals(serviceRequest.getServiceName())) {
                System.out.println("here" + entry.getValue().getWebSocket());
                toReturn.put(entry.getKey(), entry.getValue());
            }
        }
        return toReturn;

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
