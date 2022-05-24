package job.hamo.library.exception;

public class BookCollectionIdNotFoundException extends EntityIdNotFoundException {
    public BookCollectionIdNotFoundException(Long id) {
        super(id);
    }
}
