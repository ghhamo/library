package job.hamo.library.controller;

import job.hamo.library.dto.CreateBookListDTO;
import job.hamo.library.dto.CreateListRelationshipDTO;
import job.hamo.library.entity.BookList;
import job.hamo.library.service.BookListService;
import job.hamo.library.dto.BookListDTO;
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
@RequestMapping("/api/bookLists")
public class BookListController {

    @Autowired
    private BookListService bookListService;

    @Autowired
    private CSVParser csvParser;

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateBookListDTO> bookListDTOS = bookListService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name,user_id");
        for (CreateBookListDTO bookListDTO : bookListDTOS) {
            csvBuilder.append("\n")
                    .append(bookListDTO.id())
                    .append(',')
                    .append(bookListDTO.name())
                    .append(',')
                    .append(bookListDTO.userId());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/export/relationships")
    public ResponseEntity<String> exportRelationship() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateListRelationshipDTO> relationshipDTOS = bookListService.exportAllRelationship();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("list_id,book_id");
        for (CreateListRelationshipDTO relationshipDTO : relationshipDTOS) {
            csvBuilder.append("\n")
                    .append(relationshipDTO.listId())
                    .append(',')
                    .append(relationshipDTO.bookId());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/import")
    public Iterable<CreateBookListDTO> importBookList(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "bookList");
        List<CreateBookListDTO> bookListDTOS = new ArrayList<>();
        for (String[] row : rows) {
            BookList bookList = bookListService.csvToBookList(row);
            bookListDTOS.add(CreateBookListDTO.fromBookList(bookList));
        }
        return bookListService.importBookLists(bookListDTOS);
    }

    @PostMapping("import/relationships")
    public Iterable<CreateListRelationshipDTO> importBookListRelationship(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "listBooks");
        List<CreateListRelationshipDTO> relationshipDTOS = new ArrayList<>();
        for (String[] row : rows) {
            relationshipDTOS.add(CreateListRelationshipDTO.toCreateListRelationshipDTO(row[0], row[1]));
        }
        return bookListService.importBookListRelationships(relationshipDTOS);
    }

    @GetMapping
    public Iterable<BookListDTO> getAll() {
        return bookListService.getAllLists();
    }


    @PostMapping
    public ResponseEntity<BookListDTO> create(@RequestBody CreateBookListDTO bookListDto) {
        return ResponseEntity.ok().body(bookListService.create(bookListDto));
    }

    @GetMapping("/{id}")
    public BookListDTO getOne(@PathVariable UUID id) {
        return bookListService.getListById(id);
    }

    @PutMapping("/{id}")
    public BookListDTO updateBookList(@PathVariable UUID id) {
        return bookListService.updateList(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        bookListService.deleteList(id);
    }

    @GetMapping("/books/{id}")
    public Iterable<BookListDTO> getListsWhereBookExist(@PathVariable UUID id) {
        return bookListService.getListsWhereBookExist(id);
    }

    @PostMapping("/{list_id}/books/{book_id}")
    public BookListDTO addBookToList(@PathVariable UUID list_id, @PathVariable UUID book_id) {
        return bookListService.addBookToList(list_id, book_id);
    }

}
