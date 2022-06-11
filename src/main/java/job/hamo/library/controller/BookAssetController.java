package job.hamo.library.controller;

import job.hamo.library.dto.UploadResponseDTO;
import job.hamo.library.service.BookAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/bookAssets")
public class BookAssetController {

    private final BookAssetService bookAssetService;

    @Autowired
    public BookAssetController(BookAssetService bookAssetService) {
        this.bookAssetService = bookAssetService;
    }

    @GetMapping("/upload")
    public ResponseEntity<Resource> uploadFile(@RequestParam("id") Long id, HttpServletRequest request) {
        UploadResponseDTO uploadResponseDTO = bookAssetService.getResourcesOfUploadResponse(id, request);
        Resource resource = uploadResponseDTO.resource();
        String contentType = uploadResponseDTO.contentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
