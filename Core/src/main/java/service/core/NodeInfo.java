package service.core;

import oshi.SystemInfo;
import org.java_websocket.WebSocket;
import java.util.UUID;

public class NodeInfo extends Message{
    UUID systemID;
    SystemInfo systemInfo;
    WebSocket webSocket;
    String serviceName;

    public NodeInfo() {
        super(Message.MessageTypes.NODE_INFO);
    }

    public NodeInfo(UUID systemID, SystemInfo systemInfo, String serviceName) {
        super(Message.MessageTypes.NODE_INFO);
        this.systemID=systemID;
        this.systemInfo=systemInfo;
        this.serviceName=serviceName;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
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
    //currently assume all nodes have same operating specifications
    // this may be expanded in future to provide CPU,Ram etc.
}
