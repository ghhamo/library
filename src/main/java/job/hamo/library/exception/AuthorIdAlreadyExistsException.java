package job.hamo.library.exception;

public class AuthorIdAlreadyExistsException extends EntityIdAlreadyExists {

    public AuthorIdAlreadyExistsException(Long id) {
        super(id);
    }
}
