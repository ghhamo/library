package job.hamo.library.service;

import job.hamo.library.dto.BookJoinQueryDTO;
import job.hamo.library.dto.CreateBookDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.*;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class BookService {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    private final GenreRepository genreRepository;
    private final BookImageUrlGenerator bookImageUrlGenerator;
    private final ImageDownloader imageDownloader;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    @Autowired
    public BookService(GenreRepository genreRepository, BookImageUrlGenerator bookImageUrlGenerator,
                       ImageDownloader imageDownloader, BookRepository bookRepository,
                       AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.genreRepository = genreRepository;
        this.bookImageUrlGenerator = bookImageUrlGenerator;
        this.imageDownloader = imageDownloader;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    public Map<String, Book> booksMapToMap(Collection<Book> list) {
        Map<String, Book> books = new HashMap<>();
        for (Book book : list) {
            books.put(book.getIsbn().toLowerCase(), book);
        }
        return books;
    }

    public Iterable<BookDTO> getBooksByAuthorName(String authorName, PaginationDTO paginationDTO) {
        Objects.requireNonNull(authorName);
        authorRepository.findByName(authorName).orElseThrow(() -> new AuthorNameNotFoundException(authorName));
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<Book> books = bookRepository.findAllByAuthorName(authorName, pageRequest);
        return BookDTO.mapBookSetToBookDto(books);
    }

    public Iterable<BookDTO> getBooksByPublisherName(String publisherName, PaginationDTO paginationDTO) {
        Objects.requireNonNull(publisherName);
        publisherRepository.findByName(publisherName).orElseThrow(() -> new PublisherNameNotFoundException(publisherName));
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<Book> books = bookRepository.findAllByPublisherName(publisherName, pageRequest);
        return BookDTO.mapBookSetToBookDto(books);
    }

    public Iterable<BookDTO> getBooks(PaginationDTO paginationDTO) {
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<Book> books = bookRepository.findAll(pageRequest);
        return BookDTO.mapBookSetToBookDto(books);
    }

    public Iterable<BookJoinQueryDTO> getAllBooksByAuthorAndPublisherIds(int limit) {
        List<Object[]> results = bookRepository.findAllBooksByAuthorAndPublisherIds(limit);
        return BookJoinQueryDTO.objectToBookJoinQueryDTO(results);
    }

    public BookDTO create(CreateBookDTO createBookDTO) {
        Objects.requireNonNull(createBookDTO);
        Objects.requireNonNull(createBookDTO.getISBN());
        Objects.requireNonNull(createBookDTO.getTitle());
        Objects.requireNonNull(createBookDTO.getYearOfPublication());
        Objects.requireNonNull(createBookDTO.getPublisherId());
        Objects.requireNonNull(createBookDTO.getGenreId());
        Objects.requireNonNull(createBookDTO.getAuthorId());
        Book book = CreateBookDTO.toBook(createBookDTO);
        Author author = authorRepository.findById(createBookDTO.getAuthorId()).orElseThrow(() -> new AuthorIdNotFoundException(createBookDTO.getAuthorId()));
        Genre genre = genreRepository.findById(createBookDTO.getGenreId()).orElseThrow(() -> new GenreIdNotFoundException(createBookDTO.getGenreId()));
        Publisher publisher = publisherRepository.findById(createBookDTO.getPublisherId()).orElseThrow(() -> new PublisherIdNotFoundException(createBookDTO.getPublisherId()));
        book.setPublisher(publisher);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setImageUrlL(bookImageUrlGenerator.addBookUrl());
        Book savedBook = bookRepository.save(book);
        return BookDTO.fromBook(savedBook);
    }

    public BookDTO updateBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        book.setTitle("title");
        book.getGenre().setName("genre");
        bookRepository.save(book);
        return BookDTO.fromBook(book);
    }

    public BookDTO getBookById(Long id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        return BookDTO.fromBook(book);
    }

    public void downloadImage(Long id) throws IOException {
        Optional<Book> book = bookRepository.getBookWhereBigImageDoNotExistsById(id);
        if (book.isPresent()) {
            throw new ImageAlreadyDownloadedException();
        } else {
            imageDownloader.downloadImage(id);
        }
    }
}