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
    private String serviceOwnerProxy;//idk if this is how i wanna do it but maybe

    public ServiceResponse(){
        super(Message.MessageTypes.SERVICE_RESPONSE);
    }

    public ServiceResponse(UUID requstorID, UUID serviceOwnerID, String serviceOwnerProxy) {
        super(Message.MessageTypes.SERVICE_RESPONSE);
        this.requstorID = requstorID;
        this.serviceOwnerID = serviceOwnerID;
        this.serviceOwnerProxy = serviceOwnerProxy;
    }

    public UUID getRequstorID() {
        return requstorID;
    }

    public UUID getServiceOwnerID() {
        return serviceOwnerID;
    }

    public String getServiceOwnerAddress() {
        return serviceOwnerProxy;
    }
}
