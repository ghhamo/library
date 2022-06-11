package job.hamo.library.controller;

import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.service.BookListService;
import job.hamo.library.dto.BookListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookLists")
public class BookListController {

    private final BookListService bookListService;

    @Autowired
    public BookListController(BookListService bookListService) {
        this.bookListService = bookListService;
    }

    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookListDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return bookListService.getAllLists(new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public BookListDTO getOne(@PathVariable Long id) {
        return bookListService.getListById(id);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public BookListDTO getByName(@PathVariable String name) {
        return bookListService.getListByName(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public BookListDTO updateBookList(@PathVariable Long id) {
        return bookListService.updateList(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public void delete(@PathVariable Long id) {
        bookListService.deleteList(id);
    }

    @GetMapping("/books/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Iterable<BookListDTO> getListsWhereBookExist(@PathVariable Long id) {
        return bookListService.getListsWhereBookExist(id);
    }

    @PostMapping("/{list_id}/books/{book_id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public BookListDTO addBookToList(@PathVariable Long list_id, @PathVariable Long book_id) {
        return bookListService.addBookToList(list_id, book_id);
    }

}
