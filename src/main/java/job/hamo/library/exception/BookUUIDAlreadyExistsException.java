package job.hamo.library.exception;

import java.util.UUID;

public class BookUUIDAlreadyExistsException extends EntityUUDAlreadyExists {
    public BookUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
