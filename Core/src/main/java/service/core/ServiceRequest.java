package service.core;

import java.util.UUID;

public class ServiceRequest extends Message{
    private UUID requstorID;
    private String serviceName;

    public ServiceRequest(){
        super(Message.MessageTypes.SERVICE_REQUEST);
    }
    public ServiceRequest(UUID requstorID,String serviceName) {
        super(Message.MessageTypes.SERVICE_REQUEST);
        this.requstorID=requstorID;
        this.serviceName=serviceName;
    }

    public UUID getRequstorID() {
        return requstorID;
    }

    public String getServiceName() {
        return serviceName;
    }

}
