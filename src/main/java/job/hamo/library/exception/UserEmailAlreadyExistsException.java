package job.hamo.library.exception;

public class UserEmailAlreadyExistsException extends EntityNameAlreadyExistsException {
    public UserEmailAlreadyExistsException(String name) {
        super(name);
    }
}
