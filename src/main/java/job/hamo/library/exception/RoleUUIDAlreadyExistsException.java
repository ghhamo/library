package job.hamo.library.exception;

import java.util.UUID;

public class RoleUUIDAlreadyExistsException extends EntityUUDAlreadyExists {
    public RoleUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
