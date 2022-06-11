package job.hamo.library.exception;

public class AuthorNameAlreadyExistsException extends EntityNameAlreadyExistsException {
    public AuthorNameAlreadyExistsException(String name) {
        super(name);
    }
}
