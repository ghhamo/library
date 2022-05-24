package job.hamo.library.service;

import job.hamo.library.dto.*;
import job.hamo.library.entity.*;
import job.hamo.library.entity.BookCollection;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.repository.BookCollectionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class BookCollectionService {

    @Autowired
    private BookCollectionRepository bookCollectionRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

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

    public Iterable<BookDTO> getBooksOfCollection(Long collection_id) {
        Objects.requireNonNull(collection_id);
        BookCollection bookCollection = bookCollectionRepository.findById(collection_id)
                .orElseThrow(() -> new BookCollectionIdNotFoundException(collection_id));
        return BookDTO.mapBookSetToBookDto(bookCollection.getBooks());
    }

    private void createRelationship(CreateCollectionRelationshipDTO relationshipDTO) {
        Objects.requireNonNull(relationshipDTO);
        Objects.requireNonNull(relationshipDTO.bookId());
        Objects.requireNonNull(relationshipDTO.collectionId());
        BookCollection bookCollection = bookCollectionRepository.findById(relationshipDTO.collectionId())
                .orElseThrow(() -> new BookCollectionIdNotFoundException(relationshipDTO.collectionId()));
        Book book = bookRepository.findById(relationshipDTO.bookId()).orElseThrow(() ->
                new BookListIdNotFoundException(relationshipDTO.bookId()));
        entityManager.createNativeQuery("INSERT INTO collection_books (collection_id, book_id) VALUES(?, ?)")
                .setParameter(1, bookCollection.getId())
                .setParameter(2, book.getId())
                .executeUpdate();
    }

    @Transactional
    public BookCollectionDTO create(CreateBookCollectionDTO collectionDto) {
        Objects.requireNonNull(collectionDto);
        Objects.requireNonNull(collectionDto.name());
        Objects.requireNonNull(collectionDto.userId());
        if (collectionDto.id() != null) {
            boolean existsById = bookCollectionRepository.existsById(collectionDto.id());
            if (existsById) {
                throw new BookCollectionIdAlreadyExistsException(collectionDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO book_collection (id, name, user_id) VALUES (?,?,?)")
                    .setParameter(1, collectionDto.id())
                    .setParameter(2, collectionDto.name())
                    .setParameter(3, collectionDto.userId())
                    .executeUpdate();
        } else {
            bookCollectionRepository.save(CreateBookCollectionDTO.toBookCollection(collectionDto));
        }
        BookCollection bookCollection = CreateBookCollectionDTO.toBookCollection(collectionDto);
        BookCollection savedBookCollection = entityManager.merge(bookCollection);
        return BookCollectionDTO.fromBookCollection(savedBookCollection);
    }

    public BookCollectionDTO getCollectionById(Long id) {
        Objects.requireNonNull(id);
        BookCollection bookCollection = bookCollectionRepository.findById(id).orElseThrow(() -> new BookCollectionIdNotFoundException(id));
        return BookCollectionDTO.fromBookCollection(bookCollection);
    }

    public void deleteCollection(Long id) {
        Objects.requireNonNull(id);
        if (bookCollectionRepository.existsById(id)) {
            bookCollectionRepository.deleteById(id);
        } else throw new BookCollectionIdNotFoundException(id);
    }

    public void deleteBookOfCollection(Long collectionId, Long bookId) {
        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(bookId);
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId).orElseThrow(() ->
                new BookCollectionIdNotFoundException(collectionId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookIdNotFoundException(bookId));
        bookCollection.getBooks().remove(book);
        bookCollectionRepository.save(bookCollection);
    }

    public BookCollectionDTO updateCollection(Long id) {
        Objects.requireNonNull(id);
        BookCollection bookCollection = bookCollectionRepository.findById(id).orElseThrow(() -> new BookCollectionIdNotFoundException(id));
        bookCollection.setName("collection");
        BookCollection changedBookCollection = bookCollectionRepository.save(bookCollection);
        return BookCollectionDTO.fromBookCollection(changedBookCollection);
    }

    public BookCollectionDTO updateBookOfCollection(Long collectionId, Long bookId) {
        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(bookId);
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId).orElseThrow(() -> new BookCollectionIdNotFoundException(collectionId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookIdNotFoundException(bookId));
        if (bookCollection.getBooks().contains(book)) {
            book.setTitle("title");
            book.getGenre().setName("genre");
            bookRepository.save(book);
        } else {
            throw new BookNotFoundInCollectionException();
        }
        BookCollection changedBookCollection = bookCollectionRepository.save(bookCollection);
        return BookCollectionDTO.fromBookCollection(changedBookCollection);
    }

    public Iterable<BookCollectionDTO> getCollectionsWhereBookExist(Long id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        Iterable<BookCollection> collections = bookCollectionRepository.findAll();
        return BookCollectionDTO.mapCollectionEntityListToCollectionDtoList(collections, book);
    }

    public BookCollectionDTO addBookToCollection(Long collectionId, Long bookId) {
        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookIdNotFoundException(bookId));
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new BookCollectionIdNotFoundException(collectionId));
        if (bookCollection.getBooks().contains(book)) {
            throw new BookIdAlreadyExistsException(bookId);
        } else {
            bookCollection.getBooks().add(book);
        }
        BookCollection savedBookCollection = bookCollectionRepository.save(bookCollection);
        return BookCollectionDTO.fromBookCollection(savedBookCollection);
    }
}
