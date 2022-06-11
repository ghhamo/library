package job.hamo.library.exception;

public class AuthorIdNotFoundException extends EntityIdNotFoundException {
    public AuthorIdNotFoundException(Long id) {
        super(id);
    }
}