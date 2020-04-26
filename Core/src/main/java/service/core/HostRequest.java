package service.core;

import java.util.UUID;

public class HostRequest extends Message  {
    private UUID requstorID;
    private String serviceName;

    public HostRequest() {
        super(Message.MessageTypes.HOST_REQUEST);
    }

    public HostRequest(UUID requstorID, String serviceName) {
        super(Message.MessageTypes.HOST_REQUEST);
        this.requstorID = requstorID;
        this.serviceName = serviceName;
    }

    public UUID getRequestorID() {
        return requstorID;
    }

    public String getRequestedServiceName() {
        return serviceName;
    }
}
