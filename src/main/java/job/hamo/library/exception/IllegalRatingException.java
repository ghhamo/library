package job.hamo.library.exception;

public class IllegalRatingException extends RuntimeException {
    private final int rating;

    public IllegalRatingException(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }
}
