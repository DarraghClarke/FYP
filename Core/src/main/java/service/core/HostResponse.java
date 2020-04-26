package service.core;

import java.net.URI;
import java.util.UUID;

public class HostResponse extends Message  {
    private UUID requstorID;
    private URI serviceHostAddress;

    public HostResponse() {
        super(MessageTypes.HOST_RESPONSE);
    }

    public HostResponse(UUID requstorID, URI serviceHostAddress) {
        super(MessageTypes.HOST_RESPONSE);
        this.requstorID = requstorID;
        this.serviceHostAddress = serviceHostAddress;
    }

    public UUID getRequestorID() {
        return requstorID;
    }

    public URI getServiceHostAddress() {
        return serviceHostAddress;
    }
}
