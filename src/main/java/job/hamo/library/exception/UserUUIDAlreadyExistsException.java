package job.hamo.library.exception;

import java.util.UUID;

public class UserUUIDAlreadyExistsException extends EntityUUDAlreadyExists {
    public UserUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
