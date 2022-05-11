package job.hamo.library.exception;

import java.util.UUID;

public class BookCollectionUUIDAlreadyExistsException extends EntityUUDAlreadyExists {
    public BookCollectionUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
