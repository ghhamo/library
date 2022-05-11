package job.hamo.library.exception;

import java.util.UUID;

public class BookUUIDNotFoundException extends EntityUUIDNotFoundException {
    public BookUUIDNotFoundException(UUID uuid) {
        super(uuid);
    }
}
