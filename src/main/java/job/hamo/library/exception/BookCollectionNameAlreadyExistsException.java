package job.hamo.library.exception;

public class BookCollectionNameAlreadyExistsException extends EntityNameAlreadyExistsException {
    public BookCollectionNameAlreadyExistsException(String name) {
        super(name);
    }
}
