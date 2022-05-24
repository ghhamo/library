package job.hamo.library.exception;

public class BookIdNotFoundException extends EntityIdNotFoundException {
    public BookIdNotFoundException(Long id) {
        super(id);
    }
}
