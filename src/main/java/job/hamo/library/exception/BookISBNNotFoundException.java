package job.hamo.library.exception;

public class BookISBNNotFoundException extends EntityISBNNotFoundException {
    public BookISBNNotFoundException(String ISBN) {
        super(ISBN);
    }
}
