package job.hamo.library.dto;

import job.hamo.library.entity.Rating;

public class CreateRatingDTO {

    private final Long userId;
    private final String isbn;
    private int rating;

    public CreateRatingDTO(Long userId, String isbn, int rating) {
        this.userId = userId;
        this.isbn = isbn;
        this.rating = rating;
    }


    public static CreateRatingDTO fromRating(Rating rating) {
        return new CreateRatingDTO(
                rating.getUser().getId(),
                rating.getBook().getIsbn(),
                rating.getRating());
    }

    public Long getUserId() {
        return userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
