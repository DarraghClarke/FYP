package service.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.UUID;

//this step is kinda acting like the owner of a service acknowleding the response and approving the transfer
public class ServiceResponse extends Message {
    private UUID requstorID;
    private UUID serviceOwnerID;
    private String serviceName;
    private String serviceOwnerAddress;//idk if this is how i wanna do it but maybe

    public ServiceResponse(){
        super(Message.MessageTypes.SERVICE_RESPONSE);
    }

    public ServiceResponse(UUID requstorID, UUID serviceOwnerID, String serviceOwnerAddress,String serviceName) {
        super(Message.MessageTypes.SERVICE_RESPONSE);
        this.requstorID = requstorID;
        this.serviceOwnerID = serviceOwnerID;
        this.serviceOwnerAddress = serviceOwnerAddress;
        this.serviceName = serviceName;
    }

    public UUID getRequstorID() {
        return requstorID;
    }

    public UUID getServiceOwnerID() {
        return serviceOwnerID;
    }

    public String getServiceOwnerAddress() {
        return serviceOwnerAddress;
    }

    public String getServiceName() {
        return serviceName;
    }
}
