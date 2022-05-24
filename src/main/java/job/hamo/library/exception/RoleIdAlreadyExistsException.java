package job.hamo.library.exception;

public class RoleIdAlreadyExistsException extends EntityIdAlreadyExists {
    public RoleIdAlreadyExistsException(Long id) {
        super(id);
    }
}
