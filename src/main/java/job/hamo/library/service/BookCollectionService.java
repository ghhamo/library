package job.hamo.library.service;

import job.hamo.library.dto.*;
import job.hamo.library.entity.*;
import job.hamo.library.entity.BookCollection;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.repository.BookCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookCollectionService {

    private final BookCollectionRepository bookCollectionRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookCollectionService(BookCollectionRepository bookCollectionRepository, BookRepository bookRepository) {
        this.bookCollectionRepository = bookCollectionRepository;
        this.bookRepository = bookRepository;
    }

    public Iterable<BookCollectionDTO> getAllCollections(PaginationDTO paginationDTO) {
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<BookCollection> bookCollections = bookCollectionRepository.findAll(pageRequest);
        return BookCollectionDTO.mapCollectionListToCollectionDtoList(bookCollections);
    }

    public Iterable<BookDTO> getBooksOfCollection(Long collectionId) {
        Objects.requireNonNull(collectionId);
        BookCollection bookCollection = bookCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new BookCollectionIdNotFoundException(collectionId));
        return BookDTO.mapBookSetToBookDto(bookCollection.getBooks());
    }

    public CreateBookCollectionDTO create(CreateBookCollectionDTO collectionDto) {
        Objects.requireNonNull(collectionDto);
        Objects.requireNonNull(collectionDto.name());
        Optional<BookCollection> bookCollection = bookCollectionRepository.findByName(collectionDto.name());
        if (bookCollection.isPresent()) {
            throw new BookCollectionNameAlreadyExistsException(collectionDto.name());
        }
        BookCollection toBookCollection = CreateBookCollectionDTO.toBookCollection(collectionDto);
        BookCollection savedBookCollection = bookCollectionRepository.save(toBookCollection);
        return CreateBookCollectionDTO.fromBookCollection(savedBookCollection);

    }

    public BookCollectionDTO getCollectionByName(String name) {
        Objects.requireNonNull(name);
        BookCollection bookCollection = bookCollectionRepository.findByName(name).orElseThrow(() -> new BookCollectionNameNotFoundException(name));
        return BookCollectionDTO.fromBookCollection(bookCollection);
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
        Iterable<BookCollection> lists = bookCollectionRepository.findAll();
        return BookCollectionDTO.mapCollectionEntityListToCollectionDtoList(lists, book);
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
