package job.hamo.library.exception;

import java.util.UUID;

public class BookListUUIDAlreadyExistsException extends EntityUUDAlreadyExists{
    public BookListUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
