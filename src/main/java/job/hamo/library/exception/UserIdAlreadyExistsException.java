package job.hamo.library.exception;

public class UserIdAlreadyExistsException extends EntityIdAlreadyExists {
    public UserIdAlreadyExistsException(Long id) {
        super(id);
    }
}
