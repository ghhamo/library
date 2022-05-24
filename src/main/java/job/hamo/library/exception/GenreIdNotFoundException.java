package job.hamo.library.exception;

public class GenreIdNotFoundException extends EntityIdNotFoundException {
    public GenreIdNotFoundException(Long id) {
        super(id);
    }
}
