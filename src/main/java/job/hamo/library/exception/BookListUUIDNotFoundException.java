package job.hamo.library.exception;

import java.util.UUID;

public class BookListUUIDNotFoundException extends EntityUUIDNotFoundException {
    public BookListUUIDNotFoundException(UUID uuid) {
        super(uuid);
    }
}
