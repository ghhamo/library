package job.hamo.library.service;

import job.hamo.library.dto.CreateBookDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.*;
import job.hamo.library.dto.AuthorDTO;
import job.hamo.library.dto.BookDTO;
import job.hamo.library.util.DataGenerator;
import job.hamo.library.util.ImageDownloader;
import job.hamo.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static job.hamo.library.util.DataGenerator.randomString;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ImageDownloader imageDownloader;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<CreateBookDTO> exportAll() {
        List<Book> all = bookRepository.findAll();
        List<CreateBookDTO> result = new LinkedList<>();
        for (Book book : all) {
            result.add(CreateBookDTO.fromBook(book));
        }
        return result;
    }

    @Transactional
    public List<CreateBookDTO> importBooks(Iterable<CreateBookDTO> books) {
        List<CreateBookDTO> invalidDTOs = new LinkedList<>();
        for (CreateBookDTO createBookDTO : books) {
            if (createBookDTO == null) {
                continue;
            }
            try {
                create(createBookDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(createBookDTO);
            }
        }
        return invalidDTOs;
    }

    public Iterable<BookDTO> getAllBook() {
        Iterable<Book> books = bookRepository.findAll();
        return BookDTO.mapBookSetToBookDto(books);
    }

    public BookDTO create(CreateBookDTO createBookDTO) {
        Book book = null;
        Objects.requireNonNull(createBookDTO);
        Objects.requireNonNull(createBookDTO.authorId());
        Objects.requireNonNull(createBookDTO.genreId());
        Objects.requireNonNull(createBookDTO.title());
        Author author = authorRepository.findById(createBookDTO.authorId()).orElseThrow(() ->
                new AuthorUUIDNotFoundException(createBookDTO.authorId()));
        Genre genre = genreRepository.findById(createBookDTO.genreId()).orElseThrow(() ->
                new GenreUUIDNotFoundException(createBookDTO.genreId()));
        if (createBookDTO.bigImageUrl() == null) {
            try {
                assert false;
                book.setBigImageUrl(DataGenerator.addBookUrl());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        book = CreateBookDTO.toBook(createBookDTO);
        if (createBookDTO.id() != null) {
            boolean existsById = bookRepository.existsById(createBookDTO.id());
            if (existsById) {
                throw new BookUUIDAlreadyExistsException(createBookDTO.id());
            }
            entityManager.createNativeQuery("INSERT INTO book (id, title, big_image_url, author_id, genre_id) VALUES (?,?,?,?,?)")
                    .setParameter(1, UUIDUtil.asBytes(book.getId()))
                    .setParameter(2, book.getTitle())
                    .setParameter(3, book.getBigImageUrl())
                    .setParameter(4, UUIDUtil.asBytes(author.getId()))
                    .setParameter(5, UUIDUtil.asBytes(genre.getId()))
                    .executeUpdate();
            book = bookRepository.getById(createBookDTO.id());
        } else {
            book = bookRepository.save(CreateBookDTO.toBook(createBookDTO));
        }
        return BookDTO.fromBook(book);
    }

    public AuthorDTO getAuthorOfBook(UUID id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookUUIDNotFoundException(id));
        return AuthorDTO.fromAuthor(book.getAuthor());

    }

    public BookDTO getBookById(UUID id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookUUIDNotFoundException(id));
        return BookDTO.fromBook(book);
    }

    public BookDTO updateBook(UUID id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookUUIDNotFoundException(id));
        book.setTitle(randomString(12));
        book.getAuthor().setName(randomString(12));
        book.getGenre().setName(randomString(12));
        bookRepository.save(book);
        return BookDTO.fromBook(book);
    }

    public CreateBookDTO csvToBook(String[] bookRow) {
        return new CreateBookDTO(
                UUID.fromString(bookRow[0]),
                bookRow[1],
                bookRow[2],
                UUID.fromString(bookRow[3]),
                UUID.fromString(bookRow[4])
        );
    }

    @Transactional
    public void downloadBookImages() {
        Set<Object[]> set = bookRepository.findBookWhereImageDoNotExist();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        for (Object[] row : set) {
            executor.execute(() -> {
                try {
                    imageDownloader.downloadImage(UUIDUtil.asUUID((byte[]) row[0]), (String) row[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
    }


    @Transactional
    public void rateBook(UUID userId, UUID bookId, int userRating) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(bookId);
        if (userRating < 0 || userRating > 5) {
            throw new IllegalRatingException(userRating);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserUUIDNotFoundException(userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookUUIDNotFoundException(bookId));
        Rating rating = new Rating();
        rating.setRating(userRating);
        rating.setBook(book);
        rating.setUser(user);
        Rating ratingFromDB = ratingRepository.save(rating);
        Long countOfRating = book.getCountOfRating();
        int oldRatingOfBook = book.getRating();
        book.setRating(countRatingOfBook(countOfRating, oldRatingOfBook, userRating));
        book.getUserRatings().add(ratingFromDB);
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
}

