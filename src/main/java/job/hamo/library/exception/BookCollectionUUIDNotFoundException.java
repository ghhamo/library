package job.hamo.library.exception;

import java.util.UUID;

public class BookCollectionUUIDNotFoundException extends EntityUUIDNotFoundException{
    public BookCollectionUUIDNotFoundException(UUID uuid) {
        super(uuid);
    }
}
