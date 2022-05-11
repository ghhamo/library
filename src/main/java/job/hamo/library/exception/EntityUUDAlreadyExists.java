package job.hamo.library.exception;

import java.util.UUID;

public class EntityUUDAlreadyExists extends RuntimeException {
    private final UUID uuid;

    public EntityUUDAlreadyExists(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
