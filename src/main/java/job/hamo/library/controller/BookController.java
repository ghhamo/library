package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.entity.Rating;
import job.hamo.library.service.BookService;
import job.hamo.library.service.RatingService;
import job.hamo.library.util.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CSVParser csvParser;

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateBookDTO> createBookDTOS = bookService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,title,big_image_url,author_id,genre_id");
        for (CreateBookDTO createBookDTO : createBookDTOS) {
            csvBuilder.append("\n")
                    .append(createBookDTO.id())
                    .append(',')
                    .append(createBookDTO.title())
                    .append(',')
                    .append(createBookDTO.bigImageUrl())
                    .append(',')
                    .append(createBookDTO.authorId())
                    .append(',')
                    .append(createBookDTO.genreId());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/export/ratings")
    public ResponseEntity<String> exportRatings() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateRatingDTO> createRatingDTOS = ratingService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,rating,book_id,user_id");
        for (CreateRatingDTO createRatingDTO : createRatingDTOS) {
            csvBuilder.append("\n")
                    .append(createRatingDTO.id())
                    .append(',')
                    .append(createRatingDTO.rating())
                    .append(',')
                    .append(createRatingDTO.bookId())
                    .append(',')
                    .append(createRatingDTO.userId());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/import")
    public Iterable<CreateBookDTO> importBooks(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "book");
        List<CreateBookDTO> books = new ArrayList<>();
        for (String[] row : rows) {
            CreateBookDTO createBookDTO = bookService.csvToBook(row);
            books.add(createBookDTO);
        }
        return bookService.importBooks(books);
    }

    @PostMapping("/import/ratings")
    public Iterable<CreateRatingDTO> importRatings(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "rating");
        List<CreateRatingDTO> ratings = new ArrayList<>();
        for (String[] row : rows) {
            Rating rating = ratingService.csvToRating(row);
            ratings.add(CreateRatingDTO.fromRating(rating));
        }
        return ratingService.importRatings(ratings);
    }

    @PostMapping("/book/imageUrl")
    public ResponseEntity<HttpStatus> downloadImage() {
        bookService.downloadBookImages();
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @GetMapping
    public Iterable<BookDTO> getAll() {
        return bookService.getAllBook();
    }

    @GetMapping("/{bookId}/authors/author")
    public AuthorDTO getAuthorByBookId(@PathVariable UUID bookId) {
        return bookService.getAuthorOfBook(bookId);
    }

    @GetMapping("/{id}")
    public BookDTO getOne(@PathVariable UUID id) {
        return bookService.getBookById(id);
    }


    @PutMapping("/{id}")
    public BookDTO updateBook(@PathVariable UUID id) {
        return bookService.updateBook(id);
    }

    @PostMapping("/{id}/rating")
    @ResponseStatus(HttpStatus.OK)
    public void rateBook(@RequestBody BookRatingRequestDTO bookRatingRequestDTO, @PathVariable UUID userId, @PathVariable UUID bookId) {
        bookService.rateBook(bookRatingRequestDTO.userID(), bookId, bookRatingRequestDTO.rating());
    }
}
