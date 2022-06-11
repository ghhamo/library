package job.hamo.library.service;

import job.hamo.library.dto.UploadResponseDTO;
import job.hamo.library.entity.Book;
import job.hamo.library.entity.BookAsset;
import job.hamo.library.entity.Status;
import job.hamo.library.exception.BookAssetIdNotFoundException;
import job.hamo.library.exception.BookAssetNotFoundByBookIdAndImageSizeException;
import job.hamo.library.exception.FileTypeException;
import job.hamo.library.repository.BookAssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookAssetService {

    private final BookAssetRepository bookAssetRepository;

    @Autowired
    public BookAssetService(BookAssetRepository bookAssetRepository) {
        this.bookAssetRepository = bookAssetRepository;
    }

    public UploadResponseDTO getResourcesOfUploadResponse(Long id, HttpServletRequest request) {
        Objects.requireNonNull(id);
        Optional<BookAsset> bookAsset = bookAssetRepository.findById(id);
        if (bookAsset.isEmpty()) {
            throw new BookAssetIdNotFoundException(id);
        }
        Resource resource = null;
        try {
            resource = loadFileAsResource(bookAsset.get().getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert resource != null;
        String contentType = getContentType(resource, request);
        return new UploadResponseDTO(resource, contentType);
    }

    private Resource loadFileAsResource(String fileName) throws Exception {
        try {
            Path filePath = Path.of(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    public String getContentType(Resource resource, HttpServletRequest request) {
        try {
            return request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new FileTypeException();
        }
    }

    @Transactional
    public void updateBookAsset(String path, Long bookId, String bookAssetSize) {
        BookAsset bookAsset = switch (bookAssetSize) {
            case "big" -> bookAssetRepository.findBookAssetByBookIdImageBigSizeAndStatus(bookId);
            case "medium" -> bookAssetRepository.findBookAssetByBookIdImageMediumSizeAndStatus(bookId);
            case "small" -> bookAssetRepository.findBookAssetByBookIdImageSmallSizeAndStatus(bookId);
            default -> throw new IllegalStateException();
        };
        bookAsset.setStatus(Status.DONE.name());
        bookAsset.setDate(new Date());
        bookAsset.setPath(path);
        bookAssetRepository.save(bookAsset);
    }

    @Transactional
    public void insertOrUpdateBookAsset(long bookId, String imageSize, Status status) {
        Optional<BookAsset> bookAsset = bookAssetRepository.findByBookIdAndImageSize(bookId, imageSize);
        BookAsset bookAssetToSave;
        if (bookAsset.isPresent()) {
            bookAssetToSave = bookAsset.get();
        } else {
            bookAssetToSave = new BookAsset();
            Book book = new Book();
            book.setId(bookId);
            bookAssetToSave.setBook(book);
            bookAssetToSave.setImageSize(imageSize);
        }
        bookAssetToSave.setStatus(status.name());
        bookAssetToSave.setDate(new Date());
        bookAssetRepository.save(bookAssetToSave);
    }

    @Transactional
    public void updateBookAssetState(long bookId, String imageSize, Status status) {
        Optional<BookAsset> bookAsset = bookAssetRepository.findByBookIdAndImageSize(bookId, imageSize);
        BookAsset bookAssetToSave;
        if (bookAsset.isPresent()) {
            bookAssetToSave = bookAsset.get();
        } else {
            throw new BookAssetNotFoundByBookIdAndImageSizeException(bookId, imageSize);
        }
        bookAssetToSave.setStatus(status.name());
        bookAssetToSave.setDate(new Date());
        bookAssetRepository.save(bookAssetToSave);
    }
}
