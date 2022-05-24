package job.hamo.library.exception;

public class GenreIdAlreadyExistsException extends EntityIdAlreadyExists {
    public GenreIdAlreadyExistsException(Long id) {
        super(id);
    }
}
