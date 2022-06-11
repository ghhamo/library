package job.hamo.library.controller;


import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.service.AuthorService;
import job.hamo.library.service.BookService;
import job.hamo.library.dto.BookDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @Autowired
    public AuthorController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @GetMapping("/{name}/books")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookDTO> getBooksByAuthorName(@PathVariable String name, @RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return bookService.getBooksByAuthorName(name, new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITOR', 'SCOPE_USER')")
    public Iterable<AuthorDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return authorService.getAllAuthor(new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_USER')")
    public AuthorDTO getByName(@PathVariable String name) {
        return authorService.getAuthorByName(name);
    }


    @GetMapping("/{author_id}/books")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_USER')")
    public Iterable<BookDTO> getBooksByAuthorId(@PathVariable Long author_id, @RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return authorService.getBooksOfAuthorByAuthorId(author_id, new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_EDITOR', 'SCOPE_USER')")
    public AuthorDTO getOne(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }


    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public ResponseEntity<AuthorDTO> create(@RequestBody AuthorDTO authorDto) {
        return ResponseEntity.ok().body(authorService.create(authorDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public void delete(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public AuthorDTO updateAuthor(@PathVariable Long id) {
        return authorService.updateAuthor(id);
    }
}
