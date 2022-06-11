package job.hamo.library.exception;

public class BookAssetNotFoundByBookIdAndImageSizeException extends ValidationException {
    private final Long bookId;
    private final String imageSize;

    public BookAssetNotFoundByBookIdAndImageSizeException(Long bookId, String imageSize) {
        this.bookId = bookId;
        this.imageSize = imageSize;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getImageSize() {
        return imageSize;
    }
}
