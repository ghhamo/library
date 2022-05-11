package job.hamo.library.controller;

import job.hamo.library.dto.*;
import job.hamo.library.entity.BookCollection;
import job.hamo.library.service.BookCollectionService;
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
@RequestMapping("/api/bookCollections")
public class BookCollectionController {

    @Autowired
    private BookCollectionService bookCollectionService;

    @Autowired
    private CSVParser csvParser;

    @PostMapping("/import")
    public Iterable<CreateBookCollectionDTO> importCollections(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "bookCollection");
        List<CreateBookCollectionDTO> collectionDTOS = new ArrayList<>();
        for (String[] row : rows) {
            BookCollection bookCollection = bookCollectionService.csvToCollection(row);
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

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateBookCollectionDTO> collectionDTOS = bookCollectionService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name,user_id");
        for (CreateBookCollectionDTO collectionDTO : collectionDTOS) {
            csvBuilder.append("\n")
                    .append(collectionDTO.id())
                    .append(',')
                    .append(collectionDTO.name())
                    .append(',')
                    .append(collectionDTO.userId());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/export/relationships")
    public ResponseEntity<String> exportRelationship() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateCollectionRelationshipDTO> relationshipDTOS = bookCollectionService.exportAllRelationship();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("collection_id,book_id");
        for (CreateCollectionRelationshipDTO relationshipDTO : relationshipDTOS) {
            csvBuilder.append("\n")
                    .append(relationshipDTO.collectionId())
                    .append(',')
                    .append(relationshipDTO.bookId());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
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
    public BookCollectionDTO getOne(@PathVariable UUID id) {
        return bookCollectionService.getCollectionById(id);
    }

    @PutMapping("/{id}")
    public BookCollectionDTO updateCollection(@PathVariable UUID id) {
        return bookCollectionService.updateCollection(id);
    }

    @PutMapping("/{collection_id}/books/{book_id}")
    public BookCollectionDTO updateBookOfCollection(@PathVariable UUID collection_id, @PathVariable UUID book_id) {
        return bookCollectionService.updateBookOfCollection(collection_id, book_id);
    }

    @DeleteMapping("/{id}")
    public void deleteCollection(@PathVariable UUID id) {
        bookCollectionService.deleteCollection(id);
    }

    @DeleteMapping("/{collection_id}/{book_id}")
    public void deleteBookOfCollection(@PathVariable UUID collection_id, @PathVariable UUID book_id) {
        bookCollectionService.deleteBookOfCollection(collection_id, book_id);
    }

    @GetMapping("/{book_id}")
    public Iterable<BookCollectionDTO> getCollectionsWhereBookExist(@PathVariable UUID book_id) {
        return bookCollectionService.getCollectionsWhereBookExist(book_id);
    }

    @GetMapping("/{collection_id}/books")
    public Iterable<BookDTO> getBooksOfCollection(@PathVariable UUID collection_id) {
        return bookCollectionService.getBooksOfCollection(collection_id);
    }

    @PostMapping("/{collection_id}/books/{book_id}")
    public BookCollectionDTO addBookToCollection(@PathVariable UUID collection_id, @PathVariable UUID book_id) {
        return bookCollectionService.addBookToCollection(collection_id, book_id);
    }
}
