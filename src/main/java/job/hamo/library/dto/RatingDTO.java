package job.hamo.library.dto;
import job.hamo.library.entity.Rating;

import java.util.HashSet;
import java.util.Set;

public record RatingDTO(String userName, String userSurname, String bookTitle, int rating) {

    public static RatingDTO fromRating(Rating ratingEntity) {
        return new RatingDTO(ratingEntity.getUser().getName(), ratingEntity.getUser().getSurname(),
                ratingEntity.getBook().getTitle(), ratingEntity.getRating());
    }


    public static Set<RatingDTO> mapRatingSetToRatingDto(Iterable<Rating> ratings) {
        Set<RatingDTO> ratingDTOSet = new HashSet<>();
        for (Rating rating : ratings) {
            ratingDTOSet.add(RatingDTO.fromRating(rating));
        }
        return ratingDTOSet;
    }
}
