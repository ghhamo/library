package job.hamo.library.exception;

import java.util.UUID;

public class GenreUUIDNotFoundException extends EntityUUIDNotFoundException {
    public GenreUUIDNotFoundException(UUID uuid) {
        super(uuid);
    }
}
