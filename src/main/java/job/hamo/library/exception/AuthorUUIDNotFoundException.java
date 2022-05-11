package job.hamo.library.exception;

import java.util.UUID;

public class AuthorUUIDNotFoundException extends EntityUUIDNotFoundException {
    public AuthorUUIDNotFoundException(UUID uuid) {
        super(uuid);
    }
}