package job.hamo.library.controller;

import job.hamo.library.dto.CreateBookListDTO;
import job.hamo.library.dto.CreateListRelationshipDTO;
import job.hamo.library.entity.BookList;
import job.hamo.library.service.BookListService;
import job.hamo.library.dto.BookListDTO;
import job.hamo.library.util.CsvParser;
import job.hamo.library.util.CSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookLists")
public class BookListController {

    @Autowired
    private BookListService bookListService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private CSVUtil csvUtil;

    @PostMapping("/import")
    public Iterable<CreateBookListDTO> importBookList(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "bookList");
        List<CreateBookListDTO> bookListDTOS = new ArrayList<>();
        for (String[] row : rows) {
            BookList bookList = csvUtil.csvToBookList(row);
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
    public BookListDTO getOne(@PathVariable Long id) {
        return bookListService.getListById(id);
    }

    @PutMapping("/{id}")
    public BookListDTO updateBookList(@PathVariable Long id) {
        return bookListService.updateList(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookListService.deleteList(id);
    }

    @GetMapping("/books/{id}")
    public Iterable<BookListDTO> getListsWhereBookExist(@PathVariable Long id) {
        return bookListService.getListsWhereBookExist(id);
    }

    @PostMapping("/{list_id}/books/{book_id}")
    public BookListDTO addBookToList(@PathVariable Long list_id, @PathVariable Long book_id) {
        return bookListService.addBookToList(list_id, book_id);
    }

}
