package job.hamo.library.exception;

public class BookCollectionIdAlreadyExistsException extends EntityIdAlreadyExists {
    public BookCollectionIdAlreadyExistsException(Long id) {
        super(id);
    }
}
