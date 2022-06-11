package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.service.BookListService;
import job.hamo.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BookListService bookListService;

    @Autowired
    public UserController(UserService userService, BookListService bookListService) {
        this.userService = userService;
        this.bookListService = bookListService;
    }

    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<HttpStatus> createUser(@RequestBody UserDTO user) throws Exception {
        userService.createUser(user);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ResponseEntity<BookListDTO> createBookList(@RequestBody CreateBookListDTO bookListDto, @PathVariable Long id) {
        return ResponseEntity.ok().body(bookListService.create(bookListDto, id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public Iterable<UserDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return userService.getUsers(new PaginationDTO(pageIndex, pageSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public UserDTO getOne(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public UserDTO getByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public UserDTO updateUser(@PathVariable Long id) {
        return userService.updateUser(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}