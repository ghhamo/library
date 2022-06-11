package job.hamo.library.dto;

import org.springframework.core.io.Resource;

public record UploadResponseDTO(Resource resource, String contentType) {
}
