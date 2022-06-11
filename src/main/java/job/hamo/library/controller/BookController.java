package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.service.BookService;
import job.hamo.library.service.RatingService;
import job.hamo.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;
    private final RatingService ratingService;

    @Autowired
    public BookController(BookService bookService, UserService userService, RatingService ratingService) {
        this.bookService = bookService;
        this.userService = userService;
        this.ratingService = ratingService;
    }
    
    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public void create(@RequestBody CreateBookDTO bookDTO) {
        bookService.create(bookDTO);
    }

    @PostMapping("/book/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<HttpStatus> downloadImage(@PathVariable Long id) throws IOException {
        bookService.downloadImage(id);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageSize > pageMaxSize) {
            throw new IllegalStateException();
        }
        return bookService.getBooks(new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public BookDTO getOne(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/limit/{limit}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookJoinQueryDTO> getAllBookSByAuthorAndPublisherIds(@PathVariable int limit) {
        return bookService.getAllBooksByAuthorAndPublisherIds(limit);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public BookDTO updateBook(@PathVariable Long id) {
        return bookService.updateBook(id);
    }

    @PostMapping("/{book_id}/rate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public void rateBook(@RequestBody BookRatingRequestDTO bookRatingRequestDTO, @PathVariable Long book_id) {
        userService.rateBook(bookRatingRequestDTO.userId(), book_id, bookRatingRequestDTO.rating());
    }

    @GetMapping("/{id}/rating")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<RatingDTO> getRatingsOfBook(@PathVariable Long id) {
        return ratingService.getRatingsOfBook(id);
    }
}