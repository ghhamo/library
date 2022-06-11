package job.hamo.library.exception;

public class EntityIdAlreadyExists extends RuntimeException {
    private final Long id;

    public EntityIdAlreadyExists(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
