package job.hamo.library.exception;

public class BookIdAlreadyExistsException extends EntityIdAlreadyExists {
    public BookIdAlreadyExistsException(Long id) {
        super(id);
    }
}
