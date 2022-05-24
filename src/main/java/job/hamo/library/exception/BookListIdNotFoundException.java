package job.hamo.library.exception;

public class BookListIdNotFoundException extends EntityIdNotFoundException {
    public BookListIdNotFoundException(Long id) {
        super(id);
    }
}
