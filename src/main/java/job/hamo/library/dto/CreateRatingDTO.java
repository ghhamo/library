package job.hamo.library.dto;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.Rating;
import job.hamo.library.entity.User;

public class CreateRatingDTO {

    private Long id;
    private final Long userId;
    private final String isbn;
    private int rating;

    public CreateRatingDTO(Long userId, String isbn, int rating) {
        this.id = null;
        this.userId = userId;
        this.isbn = isbn;
        this.rating = rating;
    }

    public CreateRatingDTO(Long id, Long userId, String isbn, int rating) {
        this.id = id;
        this.userId = userId;
        this.isbn = isbn;
        this.rating = rating;
    }

    public static Rating toRating(CreateRatingDTO createRatingDTO, Book book, User user) {
        Rating rating = new Rating();
        rating.setId(createRatingDTO.id);
        rating.setRating(createRatingDTO.rating);
        rating.setBook(book);
        rating.setUser(user);
        return rating;
    }
    public  static CreateRatingDTO toCreateRatingDTO(String[] row) {
        return new CreateRatingDTO(Long.parseLong(row[0]), row[1], Integer.parseInt(row[2]));
    }

    public static CreateRatingDTO fromRating(Rating rating) {
        return new CreateRatingDTO(
                rating.getUser().getId(),
                rating.getBook().getIsbn(),
                rating.getRating());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
