package service.core;

import java.io.File;
import java.util.UUID;

public class Service extends Message{
    UUID systemID;//the UUID of the current owner of the service
    File service;
    UUID sendToID;//this is for sending the service to someone else

    public Service(UUID systemID,File service){
        super(MessageTypes.SERVICE);
        this.systemID=systemID;
        this.service=service;
    }

    public File getService() {
        return service;
    }

    public void setService(File service) {
        this.service = service;
    }
}
