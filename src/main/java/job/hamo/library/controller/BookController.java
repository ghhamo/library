package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.service.BookService;
import job.hamo.library.service.RatingService;
import job.hamo.library.util.CsvParser;
import job.hamo.library.util.CSVUtil;
import job.hamo.library.util.ImageDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private ImageDownloader imageDownloader;

    @Autowired
    private CSVUtil csvUtil;

    @PostMapping("/import")
    public void importBooks(@RequestParam("file") MultipartFile file) {

        bookService.importAuthorsPublisherAndBooks(file);
    }

    @PostMapping("/import/ratings")
    public void importRatings(@RequestParam("file") MultipartFile file) {
//        ratingService.importRatings(file);
    }


    @PostMapping("/book/{id}")
    public ResponseEntity<HttpStatus> downloadImage(@PathVariable Long id) throws IOException {
        bookService.downloadImage(id);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<BookDTO> getAll() {
        return bookService.getAllBook();
    }

    @GetMapping("/{id}")
    public BookDTO getOne(@PathVariable Long id) {
        return bookService.getBookById(id);
    }


    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable Long id) {
        return bookService.updateBook(id);
    }

    @PostMapping("/{id}/rating")
    @ResponseStatus(HttpStatus.OK)
    public void rateBook(@RequestBody BookRatingRequestDTO bookRatingRequestDTO, @PathVariable Long userId, @PathVariable Long bookId) {
        bookService.rateBook(bookRatingRequestDTO.userID(), bookId, bookRatingRequestDTO.rating());
    }
}