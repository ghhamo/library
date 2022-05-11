package job.hamo.library.exception;

import java.util.UUID;

public class RoleNameNotFoundException extends EntityNameNotFoundException {
    public RoleNameNotFoundException(String name) {
        super(name);
    }
}
