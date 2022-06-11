package job.hamo.library.controller;

import job.hamo.library.dto.BookDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.dto.PublisherDTO;
import job.hamo.library.service.BookService;
import job.hamo.library.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;
    private final BookService bookService;

    @Autowired
    public PublisherController(PublisherService publisherService, BookService bookService) {
        this.publisherService = publisherService;
        this.bookService = bookService;
    }

    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public Iterable<PublisherDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return publisherService.getAllPublisher(new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping("/{name}/books")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public Iterable<BookDTO> getBooksByPublisherName(@PathVariable String name, @RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return bookService.getBooksByPublisherName(name, new PaginationDTO(pageIndex, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public ResponseEntity<PublisherDTO> create(@RequestBody PublisherDTO publisherDto) {
        return ResponseEntity.ok().body(publisherService.create(publisherDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public PublisherDTO getOne(@PathVariable Long id) {
        return publisherService.getPublisherById(id);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public PublisherDTO getByName(@PathVariable String name) {
        return publisherService.getPublisherByName(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public PublisherDTO updatePublisher(@PathVariable Long id) {
        return publisherService.updatePublisher(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public void delete(@PathVariable Long id) {
        publisherService.deletePublisher(id);
    }
}
