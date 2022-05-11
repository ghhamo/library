package job.hamo.library.util;

import job.hamo.library.entity.*;
import job.hamo.library.entity.BookCollection;
import job.hamo.library.repository.*;
import job.hamo.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.Random;

@Component
public class DataGenerator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookCollectionRepository bookCollectionRepository;

    @Autowired
    private BookListRepository bookListRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final SecureRandom secureRnd = new SecureRandom();

    private static final String lettersAndNumbers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public void generateRandomData() {
        Random randomNum = new Random();
        List<User> users = new ArrayList<>();
        List<Author> authors = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        List<Rating> ratings = new ArrayList<>();
        List<BookList> bookLists = new ArrayList<>();
        List<BookCollection> bookCollections = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            genres.add(generateRandomGenre());
        }
        genres = new ArrayList<>(genreRepository.saveAll(genres));

        for (int i = 0; i < 50; i++) {
            authors.add(generateRandomAuthor());
        }
        authors = new ArrayList<>(authorRepository.saveAll(authors));

        for (int i = 0; i < 50; i++) {
            books.add(generateRandomBook(authors.get(randomNum.nextInt(49 - 1)),
                    genres.get(randomNum.nextInt(49 - 1))));
        }
        books = new ArrayList<>(bookRepository.saveAll(books));

        for (int i = 0; i < 30; i++) {
            users.add(generateRandomUser());
        }
        users = new ArrayList<>(userRepository.saveAll(users));

        for (int i = 0; i < 50; i++) {
            ratings.add(generateRandomRating(users.get(randomNum.nextInt(29 - 1)),
                    books.get(randomNum.nextInt(49 - 1)), randomNum.nextInt(4)));
        }
        ratings = new ArrayList<>(ratingRepository.saveAll(ratings));

        for (int i = 0; i < 50; i++) {
            Set<Book> bookSet = new HashSet<>();
            for (int j = 0; j < 10; j++) {
                bookSet.add(books.get(randomNum.nextInt(49 - 1)));
            }
            bookCollections.add(generateRandomCollection(users.get(randomNum.nextInt(29 - 1)), bookSet));
        }
        bookCollections = new ArrayList<>(bookCollectionRepository.saveAll(bookCollections));

        for (int i = 0; i < 50; i++) {
            Set<Book> bookSet = new HashSet<>();
            for (int j = 0; j < 10; j++) {
                bookSet.add(books.get(randomNum.nextInt(49 - 1)));
            }
            bookLists.add(generateRandomBookList(users.get(randomNum.nextInt(29 - 1)), bookSet));
        }
        bookLists = new ArrayList<>(bookListRepository.saveAll(bookLists));
    }

    private BookList generateRandomBookList(User user, Collection<Book> books) {
        while (true) {
            try {
                BookList bookList = new BookList();
                bookList.setName(randomString(12));
                bookList.setBooks(books.stream().toList());
                bookList.setUser(user);
                return bookList;
            } catch (Exception ignored) {
            }
        }
    }

    private BookCollection generateRandomCollection(User user, Set<Book> bookSet) {
        while (true) {
            try {
                BookCollection bookCollection = new BookCollection();
                bookCollection.setName(randomString(12));
                bookCollection.setId(UUID.randomUUID());
                bookCollection.setBooks(bookSet);
                bookCollection.setUser(user);
                return bookCollection;
            } catch (Exception ignored) {
            }
        }
    }

    private Rating generateRandomRating(User user, Book book, int userRating) {
        while (true) {
            try {
                Rating rating = new Rating();
                rating.setUser(user);
                rating.setId(UUID.randomUUID());
                rating.setBook(book);
                rating.setRating(userRating);
                return rating;
            } catch (Exception ignored) {
            }
        }
    }

    private User generateRandomUser() {
        String[] roles = new String[]{"ROLE_USER", "ROLE_EDITOR"};
        while (true) {
            try {
                User user = new User();
                user.setName(randomString(12));
                user.setId(UUID.randomUUID());
                user.setSurname(randomString(9));
                String pass = randomString(16);
                String email = randomString(12) + "@" + randomString(5) + "." + randomString(3);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(pass));
                user.setEnabled(true);
                Optional<Role> role = roleRepository.findByName(roles[(int) (Math.random() + 0.5)]);
                role.ifPresent(user::setRole);
                return user;
            } catch (Exception ignored) {
            }
        }
    }

    private Book generateRandomBook(Author author, Genre genre) {
        Book book = new Book();
        book.setTitle(randomString(12));
        book.setAuthor(author);
        book.setId(UUID.randomUUID());
        book.setGenre(genre);
        book.setRating(0);
        book.setCountOfRating(0L);
        try {
            book.setBigImageUrl(addBookUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    private Author generateRandomAuthor() {
        while (true) {
            try {
                Author author = new Author();
                author.setName(randomString(12));
                author.setId(UUID.randomUUID());
                return author;
            } catch (Exception ignored) {
            }
        }
    }

    private Genre generateRandomGenre() {
        while (true) {
            try {
                Genre genre = new Genre();
                genre.setId(UUID.randomUUID());
                genre.setName(randomString(12));
                return genre;
            } catch (Exception ignored) {
            }
        }
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(lettersAndNumbers.charAt(secureRnd.nextInt(lettersAndNumbers.length())));
        }
        return sb.toString();
    }

    public static String addBookUrl() throws IOException {
        URL url = new URL("https://picsum.photos/1920/1080");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        String location = connection.getHeaderField("location");
        connection.disconnect();
        return location;
    }
}
