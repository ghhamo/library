package job.hamo.library.dto;

import job.hamo.library.entity.Book;
import job.hamo.library.entity.Rating;
import job.hamo.library.entity.User;

import java.util.UUID;

public record CreateRatingDTO(UUID id, int rating, UUID bookId, UUID userId) {

    public static CreateRatingDTO fromRating(Rating rating) {
        return new CreateRatingDTO(rating.getId(), rating.getRating(),
                rating.getBook().getId(), rating.getUser().getId());
    }

    public static Rating toRating(CreateRatingDTO createRatingDTO) {
        Rating rating = new Rating();
        rating.setId(createRatingDTO.id);
        rating.setRating(createRatingDTO.rating);
        Book book = new Book();
        book.setId(createRatingDTO.bookId);
        rating.setBook(book);
        User user = new User();
        user.setId(createRatingDTO.userId);
        rating.setUser(user);
        return rating;
    }
}
