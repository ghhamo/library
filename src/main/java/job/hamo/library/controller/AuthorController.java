package job.hamo.library.controller;


import job.hamo.library.dto.CreateBookDTO;
import job.hamo.library.entity.Author;
import job.hamo.library.service.AuthorService;
import job.hamo.library.service.BookService;
import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.BookDTO;
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
@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CSVParser csvParser;

    @GetMapping
    public Iterable<AuthorDTO> getAll() {
        return authorService.getAllAuthor();
    }

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<AuthorDTO> authorDTOS = authorService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name");
        for (AuthorDTO authorDTO : authorDTOS) {
            csvBuilder.append("\n")
                    .append(authorDTO.id())
                    .append(',')
                    .append(authorDTO.name());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/import")
    public Iterable<AuthorDTO> importAuthors(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "author");
        List<AuthorDTO> authors = new ArrayList<>();
        for (String[] row : rows) {
            Author author = authorService.csvToAuthor(row);
            authors.add(AuthorDTO.fromAuthor(author));
        }
        return authorService.importAuthors(authors);
    }

    @GetMapping("/{author_id}/books")
    public Iterable<BookDTO> getBooksByAuthorId(@PathVariable UUID author_id) {
        return authorService.getBooksOfAuthor(author_id);
    }

    @GetMapping("/{id}")
    public AuthorDTO getOne(@PathVariable UUID id) {
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
    public void delete(@PathVariable UUID id) {
        authorService.deleteAuthor(id);
    }

    @PatchMapping("/{id}")
    public AuthorDTO updateAuthor(@PathVariable UUID id) {
        return authorService.updateAuthor(id);
    }
}
