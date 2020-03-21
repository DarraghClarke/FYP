package service.core;

import oshi.SystemInfo;
import org.java_websocket.WebSocket;

import java.util.Map;
import java.util.UUID;

public class NodeInfo extends Message{
    UUID systemID;
    WebSocket webSocket;
    String serviceName;
    Map<Integer,Double> CPUload;

    public NodeInfo() {
        super(Message.MessageTypes.NODE_INFO);
    }

    public NodeInfo(UUID systemID,Map<Integer,Double> CPUload , String serviceName) {
        super(Message.MessageTypes.NODE_INFO);
        this.systemID=systemID;
        this.CPUload=CPUload;
        this.serviceName=serviceName;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public String getServiceName() {
        return serviceName;
    }

    public UUID getSystemID() {
        return systemID;
    }

    public Map<Integer, Double> getCPUload() {
        return CPUload;
    }

    public void setCPUload(Map<Integer, Double> CPUload) {
        this.CPUload = CPUload;
    }
}
