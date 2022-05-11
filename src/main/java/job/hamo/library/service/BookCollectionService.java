package job.hamo.library.service;

import job.hamo.library.dto.*;
import job.hamo.library.entity.*;
import job.hamo.library.entity.BookCollection;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.repository.BookCollectionRepository;
import job.hamo.library.util.CSVParser;
import job.hamo.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static job.hamo.library.util.DataGenerator.randomString;


@Service
public class BookCollectionService {

    @Autowired
    private BookCollectionRepository bookCollectionRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<CreateBookCollectionDTO> exportAll() {
        List<BookCollection> all = bookCollectionRepository.findAll();
        List<CreateBookCollectionDTO> result = new LinkedList<>();
        for (BookCollection bookCollection : all) {
            result.add(CreateBookCollectionDTO.fromBookCollection(bookCollection));
        }
        return result;
    }

    @Transactional
    public List<CreateCollectionRelationshipDTO> exportAllRelationship() {
        Set<Object[]> relationships = bookCollectionRepository.findAllRelationship();
        List<CreateCollectionRelationshipDTO> result = new ArrayList<>();
        for (Object[] relationship : relationships) {
            result.add(CreateCollectionRelationshipDTO.makeRelationship(relationship[0], relationship[1]));
        }
        return result;
    }

    @Transactional
    public List<CreateBookCollectionDTO> importBookCollections(Iterable<CreateBookCollectionDTO> collections) {
        List<CreateBookCollectionDTO> invalidDTOs = new LinkedList<>();
        for (CreateBookCollectionDTO collectionDTO : collections) {
            if (collectionDTO == null) {
                continue;
            }
            try {
                create(collectionDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(collectionDTO);
            }
        }
        return invalidDTOs;
    }

    @Transactional
    public List<CreateCollectionRelationshipDTO> importCollectionRelationship(Iterable<CreateCollectionRelationshipDTO> relationshipDTOS) {
        List<CreateCollectionRelationshipDTO> invalidDTOs = new LinkedList<>();
        for (CreateCollectionRelationshipDTO relationshipDTO : relationshipDTOS) {
            if (relationshipDTO == null) {
                continue;
            }
            try {
                createRelationship(relationshipDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(relationshipDTO);
            }
        }
        return invalidDTOs;
    }

    public Iterable<BookCollectionDTO> getAllCollections() {
        Iterable<BookCollection> collections = bookCollectionRepository.findAll();
        return BookCollectionDTO.mapCollectionListToCollectionDtoList(collections);
    }

    public Iterable<BookDTO> getBooksOfCollection(UUID collection_id) {
        Objects.requireNonNull(collection_id);
        BookCollection bookCollection = bookCollectionRepository.findById(collection_id)
                .orElseThrow(() -> new BookCollectionUUIDNotFoundException(collection_id));
        return BookDTO.mapBookSetToBookDto(bookCollection.getBooks());
    }

    private void createRelationship(CreateCollectionRelationshipDTO relationshipDTO) {
        Objects.requireNonNull(relationshipDTO);
        Objects.requireNonNull(relationshipDTO.bookId());
        Objects.requireNonNull(relationshipDTO.collectionId());
        BookCollection bookCollection = bookCollectionRepository.findById(relationshipDTO.collectionId())
                .orElseThrow(() -> new BookCollectionUUIDNotFoundException(relationshipDTO.collectionId()));
        Book book = bookRepository.findById(relationshipDTO.bookId()).orElseThrow(() ->
                new BookListUUIDNotFoundException(relationshipDTO.bookId()));
        entityManager.createNativeQuery("INSERT INTO collection_books (collection_id, book_id) VALUES(?, ?)")
                .setParameter(1, UUIDUtil.asBytes(bookCollection.getId()))
                .setParameter(2, UUIDUtil.asBytes(book.getId()))
                .executeUpdate();
    }

    public BookCollectionDTO create(CreateBookCollectionDTO collectionDto) {
        Objects.requireNonNull(collectionDto);
        Objects.requireNonNull(collectionDto.name());
        Objects.requireNonNull(collectionDto.userId());
        if (collectionDto.id() != null) {
            boolean existsById = bookCollectionRepository.existsById(collectionDto.id());
            if (existsById) {
                throw new BookCollectionUUIDAlreadyExistsException(collectionDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO book_collection (id, name, user_id) VALUES (?,?,?)")
                    .setParameter(1, UUIDUtil.asBytes(collectionDto.id()))
                    .setParameter(2, collectionDto.name())
                    .setParameter(3, UUIDUtil.asBytes(collectionDto.userId()))
                    .executeUpdate();
        } else {
            bookCollectionRepository.save(CreateBookCollectionDTO.toBookCollection(collectionDto));
        }
        BookCollection bookCollection = CreateBookCollectionDTO.toBookCollection(collectionDto);
        BookCollection savedBookCollection = entityManager.merge(bookCollection);
        return BookCollectionDTO.fromBookCollection(savedBookCollection);
    }

    public BookCollectionDTO getCollectionById(UUID id) {
        Objects.requireNonNull(id);
        BookCollection bookCollection = bookCollectionRepository.findById(id).orElseThrow(() -> new BookCollectionUUIDNotFoundException(id));
        return BookCollectionDTO.fromBookCollection(bookCollection);
    }

    public void deleteCollection(UUID id) {
        Objects.requireNonNull(id);
        if (bookCollectionRepository.existsById(id)) {
            bookCollectionRepository.deleteById(id);
        } else throw new BookCollectionUUIDNotFoundException(id);
    }

    public void deleteBookOfCollection(UUID collectionId, UUID bookId) {
        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(bookId);
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId).orElseThrow(() ->
                new BookCollectionUUIDNotFoundException(collectionId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookUUIDNotFoundException(bookId));
        bookCollection.getBooks().remove(book);
        bookCollectionRepository.save(bookCollection);
    }

    public BookCollectionDTO updateCollection(UUID id) {
        Objects.requireNonNull(id);
        BookCollection bookCollection = bookCollectionRepository.findById(id).orElseThrow(() -> new BookCollectionUUIDNotFoundException(id));
        bookCollection.setName(randomString(12));
        BookCollection changedBookCollection = bookCollectionRepository.save(bookCollection);
        return BookCollectionDTO.fromBookCollection(changedBookCollection);
    }

    public BookCollectionDTO updateBookOfCollection(UUID collectionId, UUID bookId) {
        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(bookId);
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId).orElseThrow(() -> new BookCollectionUUIDNotFoundException(collectionId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookUUIDNotFoundException(bookId));
        if (bookCollection.getBooks().contains(book)) {
            book.setTitle(randomString(12));
            book.getGenre().setName(randomString(12));
            book.getAuthor().setName(randomString(12));
            bookRepository.save(book);
        } else {
            throw new BookNotFoundInCollectionException();
        }
        BookCollection changedBookCollection = bookCollectionRepository.save(bookCollection);
        return BookCollectionDTO.fromBookCollection(changedBookCollection);
    }

    public Iterable<BookCollectionDTO> getCollectionsWhereBookExist(UUID id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookUUIDNotFoundException(id));
        Iterable<BookCollection> collections = bookCollectionRepository.findAll();
        return BookCollectionDTO.mapCollectionEntityListToCollectionDtoList(collections, book);
    }

    public BookCollectionDTO addBookToCollection(UUID collectionId, UUID bookId) {
        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookUUIDNotFoundException(bookId));
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new BookCollectionUUIDNotFoundException(collectionId));
        if (bookCollection.getBooks().contains(book)) {
            throw new BookUUIDAlreadyExistsException(bookId);
        } else {
            bookCollection.getBooks().add(book);
        }
        BookCollection savedBookCollection = bookCollectionRepository.save(bookCollection);
        return BookCollectionDTO.fromBookCollection(savedBookCollection);
    }

    public BookCollection csvToCollection(String[] collectionRow) {
        BookCollection bookCollection = new BookCollection();
        bookCollection.setId(UUID.fromString(collectionRow[0]));
        bookCollection.setName(collectionRow[1]);
        User user = new User();
        user.setId(UUID.fromString(collectionRow[2]));
        bookCollection.setUser(user);
        return bookCollection;
    }
}
