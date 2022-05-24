package job.hamo.library.service;

import job.hamo.library.dto.CreateBookDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.*;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class BookService {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookImageUrlGenerator bookImageUrlGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ImageDownloader imageDownloader;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private CSVUtil csvUtil;

    @Autowired
    private BatchProcessor batchProcessor;

    public void importAuthorsPublisherAndBooks(MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "book");
        Set<Author> setAuthors = new HashSet<>();
        Set<Publisher> setPublishers = new HashSet<>();
        for (String[] row : rows) {
            Author author = new Author();
            author.setName(row[2]);
            setAuthors.add(author);
            Publisher publisher = new Publisher();
            publisher.setName(row[4]);
            setPublishers.add(publisher);
        }
//        List<Publisher> savedPublishers = publisherRepository.saveAll(setPublishers);
//        List<Author> savedAuthors = authorRepository.saveAll(setAuthors);
//        Map<String, Publisher> publishers = publisherService.publishersMapToMap(savedPublishers);
//        Map<String, Author> authors = authorService.authorsMapToMap(savedAuthors);
        Set<Book> books = new HashSet<>();
        for (String[] row : rows) {
            Book book = new Book();
            book.setIsbn(row[0]);
            book.setTitle(row[1]);
//            book.setAuthor(authors.get(row[2].toLowerCase()));
            book.setAuthor(null);
            book.setYearOfPublication(row[3]);
//            book.setPublisher(publishers.get(row[4].toLowerCase()));
            book.setPublisher(null);
            book.setImageUrlS(row[5]);
            book.setImageUrlM(row[6]);
            book.setImageUrlL(row[7]);
            books.add(book);
        }
//        books = books.stream().limit(100000).collect(Collectors.toSet());

        batchProcessor.batchInsert(books, bookRepository);
//        bookRepository.saveAll(books);
    }

    public Iterable<BookDTO> getAllBook() {
        Iterable<Book> books = bookRepository.findAll();
        return BookDTO.mapBookSetToBookDto(books);
    }

    @Transactional
    public BookDTO create(CreateBookDTO createBookDTO) {
        Book book;
        Objects.requireNonNull(createBookDTO);
        Objects.requireNonNull(createBookDTO.getISBN());
        Objects.requireNonNull(createBookDTO.getTitle());
        Objects.requireNonNull(createBookDTO.getAuthorId());
        Objects.requireNonNull(createBookDTO.getYearOfPublication());
        Objects.requireNonNull(createBookDTO.getPublisherId());
        Objects.requireNonNull(createBookDTO.getImageUrlL());
        Objects.requireNonNull(createBookDTO.getImageUrlM());
        Objects.requireNonNull(createBookDTO.getImageUrlS());
        book = CreateBookDTO.toBook(createBookDTO);

        book = bookRepository.save(CreateBookDTO.toBook(createBookDTO));

        return BookDTO.fromBook(book);
    }

    public BookDTO updateBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        book.setTitle("title");
        book.getGenre().setName("genre");
        bookRepository.save(book);
        return BookDTO.fromBook(book);
    }

    @Transactional
    public void rateBook(Long userId, Long bookId, int userRating) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(bookId);
        if (userRating < 0 || userRating > 10) {
            throw new IllegalRatingException(userRating);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException(userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookIdNotFoundException(bookId));
        Rating rating = new Rating();
        rating.setRating(userRating);
        rating.setBook(book);
        rating.setUser(user);
        Rating ratingFromDB = ratingRepository.save(rating);
        Long countOfRating = book.getCountOfRating();
        int oldRatingOfBook = book.getRating();
        book.setRating(countRatingOfBook(countOfRating, oldRatingOfBook, userRating));
        book.getRatings().add(ratingFromDB);
        bookRepository.save(book);
        user.getRatedBooks().add(ratingFromDB);
        userRepository.save(user);
    }

    private int countRatingOfBook(Long countOfRating, int oldRatingOfBook, int userRating) {
        long sumOfRating = countOfRating * oldRatingOfBook;
        countOfRating += 1;
        sumOfRating += userRating;
        return (int) (sumOfRating / countOfRating);
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