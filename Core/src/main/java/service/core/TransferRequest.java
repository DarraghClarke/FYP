package service.core;

import java.lang.reflect.Proxy;

public class TransferRequest extends Message{
    private Proxy requestorProxy;

    public TransferRequest(Proxy requestorProxy) {
        super(MessageTypes.TRANSFER_REQUEST);
        this.requestorProxy = requestorProxy;
    }

    public Proxy getRequestorProxy() {
        return requestorProxy;
    }

}
