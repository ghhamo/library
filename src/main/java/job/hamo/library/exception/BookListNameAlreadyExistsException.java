package job.hamo.library.exception;

public class BookListNameAlreadyExistsException extends EntityNameAlreadyExistsException {
    public BookListNameAlreadyExistsException(String name) {
        super(name);
    }
}
