package job.hamo.library.exception;

public class BookListIdAlreadyExistsException extends EntityIdAlreadyExists {
    public BookListIdAlreadyExistsException(Long id) {
        super(id);
    }
}
