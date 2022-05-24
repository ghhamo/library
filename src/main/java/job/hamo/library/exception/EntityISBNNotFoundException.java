package job.hamo.library.exception;

public class EntityISBNNotFoundException extends ValidationException {

    @java.io.Serial
    private static final long serialVersionUID = 2282541896638760576L;

    private final String ISBN;

    public EntityISBNNotFoundException(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getUuid() {
        return ISBN;
    }
}
