package job.hamo.library.exception;

public class GenreNameAlreadyExistsException extends EntityNameAlreadyExistsException {
    public GenreNameAlreadyExistsException(String name) {
        super(name);
    }
}
