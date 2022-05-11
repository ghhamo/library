package job.hamo.library.exception;

import java.util.UUID;

public class UserUUIDNotFoundException extends EntityUUIDNotFoundException {
    public UserUUIDNotFoundException(UUID uuid) {
        super(uuid);
    }
}
