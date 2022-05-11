package job.hamo.library.exception;

import java.util.UUID;

public class AuthorUUIDAlreadyExistsException extends EntityUUDAlreadyExists {

    public AuthorUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
