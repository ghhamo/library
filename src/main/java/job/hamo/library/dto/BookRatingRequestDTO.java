package job.hamo.library.dto;

import java.util.UUID;

public record BookRatingRequestDTO(Long userID, int rating) {
}
