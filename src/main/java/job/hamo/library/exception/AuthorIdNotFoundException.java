package job.hamo.library.exception;

import java.util.UUID;

public class AuthorIdNotFoundException extends EntityIdNotFoundException {
    public AuthorIdNotFoundException(Long id) {
        super(id);
    }
}