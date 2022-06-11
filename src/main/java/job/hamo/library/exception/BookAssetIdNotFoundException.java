package job.hamo.library.exception;

public class BookAssetIdNotFoundException extends EntityIdNotFoundException {
    public BookAssetIdNotFoundException(Long id) {
        super(id);
    }
}
