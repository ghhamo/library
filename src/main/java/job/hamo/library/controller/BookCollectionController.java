package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.entity.BookCollection;
import job.hamo.library.service.BookCollectionService;
import job.hamo.library.util.CsvParser;
import job.hamo.library.util.CSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookCollections")
public class BookCollectionController {

    @Autowired
    private BookCollectionService bookCollectionService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private CSVUtil csvUtil;

    @PostMapping("/import")
    public Iterable<CreateBookCollectionDTO> importCollections(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "bookCollection");
        List<CreateBookCollectionDTO> collectionDTOS = new ArrayList<>();
        for (String[] row : rows) {
            BookCollection bookCollection = csvUtil.csvToCollection(row);
            collectionDTOS.add(CreateBookCollectionDTO.fromBookCollection(bookCollection));
        }
        return bookCollectionService.importBookCollections(collectionDTOS);
    }

    @PostMapping("import/relationships")
    public Iterable<CreateCollectionRelationshipDTO> importCollectionRelationship(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "collectionBooks");
        List<CreateCollectionRelationshipDTO> relationshipDTOS = new ArrayList<>();
        for (String[] row : rows) {
            relationshipDTOS.add(CreateCollectionRelationshipDTO
                    .toCreateCollectionRelationshipDTO(row[0], row[1]));
        }
        return bookCollectionService.importCollectionRelationship(relationshipDTOS);
    }

    @GetMapping
    public Iterable<BookCollectionDTO> getAll() {
        return bookCollectionService.getAllCollections();
    }

    @PostMapping
    public ResponseEntity<BookCollectionDTO> create(@RequestBody CreateBookCollectionDTO collectionDto) {
        return ResponseEntity.ok().body(bookCollectionService.create(collectionDto));
    }

    @GetMapping("/{id}")
    public BookCollectionDTO getOne(@PathVariable Long id) {
        return bookCollectionService.getCollectionById(id);
    }

    @PutMapping("/{id}")
    public BookCollectionDTO updateCollection(@PathVariable Long id) {
        return bookCollectionService.updateCollection(id);
    }

    @PutMapping("/{collection_id}/books/{book_id}")
    public BookCollectionDTO updateBookOfCollection(@PathVariable Long collection_id, @PathVariable Long book_id) {
        return bookCollectionService.updateBookOfCollection(collection_id, book_id);
    }

    @DeleteMapping("/{id}")
    public void deleteCollection(@PathVariable Long id) {
        bookCollectionService.deleteCollection(id);
    }

    @DeleteMapping("/{collection_id}/{book_id}")
    public void deleteBookOfCollection(@PathVariable Long collection_id, @PathVariable Long book_id) {
        bookCollectionService.deleteBookOfCollection(collection_id, book_id);
    }

    @GetMapping("/{book_id}")
    public Iterable<BookCollectionDTO> getCollectionsWhereBookExist(@PathVariable Long book_id) {
        return bookCollectionService.getCollectionsWhereBookExist(book_id);
    }

    @GetMapping("/{collection_id}/books")
    public Iterable<BookDTO> getBooksOfCollection(@PathVariable Long collection_id) {
        return bookCollectionService.getBooksOfCollection(collection_id);
    }

    @PostMapping("/{collection_id}/books/{book_id}")
    public BookCollectionDTO addBookToCollection(@PathVariable Long collection_id, @PathVariable Long book_id) {
        return bookCollectionService.addBookToCollection(collection_id, book_id);
    }
}
