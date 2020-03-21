package service.core;

import java.util.UUID;

public class ServerHeartbeatRequest extends Message {
    UUID assignedUUID;

    public ServerHeartbeatRequest(){
        super(MessageTypes.NODE_INFO_REQUEST);
    }

    public ServerHeartbeatRequest(UUID assignedUUID) {
        super(MessageTypes.NODE_INFO_REQUEST);
        this.assignedUUID=assignedUUID;
    }

    public UUID getAssignedUUID() {
        return assignedUUID;
    }
}
