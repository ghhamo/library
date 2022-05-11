package job.hamo.library.exception;

import java.util.UUID;

public class RatingUUIDAlreadyExistsException extends EntityUUDAlreadyExists {
    public RatingUUIDAlreadyExistsException(UUID uuid) {
        super(uuid);
    }
}
