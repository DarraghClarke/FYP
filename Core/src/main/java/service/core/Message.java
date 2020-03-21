package service.core;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;

    public Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * MessageTypes – a static class of final Strings used to ensure consistency of message types in the system
     */
    public static class MessageTypes {
        final public static String NODE_INFO_REQUEST = "NodeInfoRequest";
        final public static String NODE_INFO = "NodeInfo";
        final public static String SERVICE_LIST_REQUEST = "ServiceListRequest";
        final public static String SERVICE_REQUEST = "ServiceRequest";
        final public static String SERVICE = "Service";
        final public static String SERVICE_INFO_REQUEST = "ServiceInfoRequest";
        final public static String SERVICE_RESPONSE = "ServiceResponse";
        final public static String TRANSFER_REQUEST = "TransferRequest";
        final public static String SERVER_HEARTBEAT_REQUEST = "ServerHeartbeatRequest";

    }
}
