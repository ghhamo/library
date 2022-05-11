package job.hamo.library.exception;

import java.util.UUID;

public class GenreUUIDAlreadyExistsException extends EntityUUDAlreadyExists{
    public GenreUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
