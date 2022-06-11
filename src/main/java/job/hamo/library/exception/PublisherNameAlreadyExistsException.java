package job.hamo.library.exception;

public class PublisherNameAlreadyExistsException extends EntityNameAlreadyExistsException {
    public PublisherNameAlreadyExistsException(String name) {
        super(name);
    }
}
