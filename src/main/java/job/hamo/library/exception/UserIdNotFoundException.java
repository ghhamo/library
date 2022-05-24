package job.hamo.library.exception;

public class UserIdNotFoundException extends EntityIdNotFoundException {
    public UserIdNotFoundException(Long id) {
        super(id);
    }
}
