package job.hamo.library.exception;

public class RatingIdAlreadyExistsException extends EntityIdAlreadyExists {
    public RatingIdAlreadyExistsException(Long id) {
        super(id);
    }
}
