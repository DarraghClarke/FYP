package service.core;

import org.java_websocket.WebSocket;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

public class NodeInfo extends Message {
    UUID systemID;
    WebSocket webSocket;
    String serviceName;
    Map<Integer, Double> CPUload;
    Map<Integer, Double> RamLoad;
    double rollingCPUScore;
    double rollingRamScore;
    boolean trustyworthy = true;
    URI serviceHostAddress;

    public NodeInfo() {
        super(Message.MessageTypes.NODE_INFO);
    }

    public NodeInfo(UUID systemID, Map<Integer, Double> CPUload, String serviceName) {
        super(Message.MessageTypes.NODE_INFO);
        this.systemID = systemID;
        this.CPUload = CPUload;
        this.serviceName = serviceName;
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

    public Map<Integer, Double> getRamLoad() {
        return RamLoad;
    }

    public void setTrustyworthy(boolean trustyworthy) {
        this.trustyworthy = trustyworthy;
    }

    public boolean isTrustyworthy() {
        return trustyworthy;
    }

    public double getRollingCPUScore() {
        return rollingCPUScore;
    }

    public double getRollingRamScore() {
        return rollingRamScore;
    }

    public void setRollingCPUScore(double rollingCPUScore) {
        this.rollingCPUScore = rollingCPUScore;
    }

    public void setRollingRamScore(double rollingRamScore) {
        this.rollingRamScore = rollingRamScore;
    }

    public URI getServiceHostAddress() {
        return serviceHostAddress;
    }

    public void setServiceHostAddress(URI serviceHostAddress) {
        this.serviceHostAddress = serviceHostAddress;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setCPUload(Map<Integer, Double> CPUload) {
        this.CPUload = CPUload;
    }

    public void setRamLoad(Map<Integer, Double> ramLoad) {
        RamLoad = ramLoad;
    }

}
