package job.hamo.library.dto;

import java.util.UUID;

public record BookRatingRequestDTO(UUID userID, int rating) {
}
