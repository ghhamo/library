package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.service.BookCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookCollections")
public class BookCollectionController {

    private final BookCollectionService bookCollectionService;

    @Autowired
    public BookCollectionController(BookCollectionService bookCollectionService) {
        this.bookCollectionService = bookCollectionService;
    }

    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_EDITOR', 'SCOPE_USER')")
    public Iterable<BookCollectionDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return bookCollectionService.getAllCollections(new PaginationDTO(pageIndex, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public ResponseEntity<CreateBookCollectionDTO> create(@RequestBody CreateBookCollectionDTO collectionDto) {
        return ResponseEntity.ok().body(bookCollectionService.create(collectionDto));
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasAnyAuthority('SCOPE_EDITOR', 'SCOPE_USER')")
    public BookCollectionDTO getByName(@PathVariable String name) {
        return bookCollectionService.getCollectionByName(name);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_EDITOR', 'SCOPE_USER')")
    public BookCollectionDTO getOne(@PathVariable Long id) {
        return bookCollectionService.getCollectionById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public BookCollectionDTO updateCollection(@PathVariable Long id) {
        return bookCollectionService.updateCollection(id);
    }

    @PutMapping("/{collection_id}/books/{book_id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public BookCollectionDTO updateBookOfCollection(@PathVariable Long collection_id, @PathVariable Long book_id) {
        return bookCollectionService.updateBookOfCollection(collection_id, book_id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public void deleteCollection(@PathVariable Long id) {
        bookCollectionService.deleteCollection(id);
    }

    @DeleteMapping("/{collection_id}/{book_id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public void deleteBookOfCollection(@PathVariable Long collection_id, @PathVariable Long book_id) {
        bookCollectionService.deleteBookOfCollection(collection_id, book_id);
    }

    @GetMapping("/books/{book_id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookCollectionDTO> getCollectionsWhereBookExist(@PathVariable Long book_id) {
        return bookCollectionService.getCollectionsWhereBookExist(book_id);
    }

    @GetMapping("/{collection_id}/books")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookDTO> getBooksOfCollection(@PathVariable Long collection_id) {
        return bookCollectionService.getBooksOfCollection(collection_id);
    }

    @PostMapping("/{collection_id}/books/{book_id}")
    @PreAuthorize("hasAuthority('SCOPE_EDITOR')")
    public BookCollectionDTO addBookToCollection(@PathVariable Long collection_id, @PathVariable Long book_id) {
        return bookCollectionService.addBookToCollection(collection_id, book_id);
    }
}
