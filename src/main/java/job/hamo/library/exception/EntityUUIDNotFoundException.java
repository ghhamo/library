package job.hamo.library.exception;

import java.util.UUID;

public class EntityUUIDNotFoundException extends ValidationException {
    @java.io.Serial
    private static final long serialVersionUID = -209583823754824159L;

    private final UUID uuid;

    public EntityUUIDNotFoundException(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
