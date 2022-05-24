package job.hamo.library.controller;


import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.CreateBookDTO;
import job.hamo.library.entity.Author;
import job.hamo.library.service.AuthorService;
import job.hamo.library.service.BookService;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.util.CSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CSVUtil csvUtil;

    @GetMapping
    public Iterable<AuthorDTO> getAll() {
        return authorService.getAllAuthor();
    }

    @GetMapping("/{author_id}/books")
    public Iterable<BookDTO> getBooksByAuthorId(@PathVariable Long author_id) {
        return authorService.getBooksOfAuthor(author_id);
    }

    @GetMapping("/{id}")
    public AuthorDTO getOne(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }


    @PostMapping
    public ResponseEntity<AuthorDTO> create(@RequestBody AuthorDTO authorDto) {
        return ResponseEntity.ok().body(authorService.create(authorDto));
    }

    @PostMapping("/{author_id}/book")
    public ResponseEntity<BookDTO> create(@RequestBody CreateBookDTO createBookDTO) {
        return ResponseEntity.ok().body(bookService.create(createBookDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }

    @PatchMapping("/{id}")
    public AuthorDTO updateAuthor(@PathVariable Long id) {
        return authorService.updateAuthor(id);
    }
}
