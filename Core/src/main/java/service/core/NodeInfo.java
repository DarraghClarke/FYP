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
    Map<Integer,Double> RamLoad;
    double rollingCPUScore;
    double rollinhRamScore;
    boolean trustyworthy = true;

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

    public double getRollinhRamScore() {
        return rollinhRamScore;
    }

    public void setRollingCPUScore(double rollingCPUScore) {
        this.rollingCPUScore = rollingCPUScore;
    }

    public void setRollinhRamScore(double rollinhRamScore) {
        this.rollinhRamScore = rollinhRamScore;
    }

    public void setCPUload(Map<Integer, Double> CPUload) {
        this.CPUload = CPUload;
    }

    public void setRamLoad(Map<Integer, Double> ramLoad) {
        RamLoad = ramLoad;
    }

}
