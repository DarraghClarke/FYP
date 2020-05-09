package service.core;

import java.util.UUID;

public class MigrationSuccess extends Message {
    UUID hostId;
    UUID oldHostId;
    String serviceName;

    public MigrationSuccess() {
        super(MessageTypes.MIGRATION_SUCESS);
    }

    public MigrationSuccess(UUID hostId,UUID oldHostId,String serviceName){
        super(MessageTypes.MIGRATION_SUCESS);
        this.hostId=hostId;
        this.serviceName=serviceName;
        this.oldHostId=oldHostId;
    }

    public UUID getHostId() {
        return hostId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public UUID getOldHostId() {
        return oldHostId;
    }
}


