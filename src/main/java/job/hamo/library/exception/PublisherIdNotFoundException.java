package job.hamo.library.exception;

public class PublisherIdNotFoundException extends EntityIdNotFoundException {
    public PublisherIdNotFoundException(Long id) {
        super(id);
    }
}
