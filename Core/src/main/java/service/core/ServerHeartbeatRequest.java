package service.core;

import java.util.UUID;

public class ServerHeartbeatRequest extends Message {
    UUID assignedUUID;

    public ServerHeartbeatRequest(){
        super(MessageTypes.SERVER_HEARTBEAT_REQUEST);
    }

    public ServerHeartbeatRequest(UUID assignedUUID) {
        super(MessageTypes.SERVER_HEARTBEAT_REQUEST);
        this.assignedUUID=assignedUUID;
    }

    public UUID getAssignedUUID() {
        return assignedUUID;
    }
}
