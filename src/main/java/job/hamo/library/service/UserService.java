package job.hamo.library.service;

import job.hamo.library.SetupDataLoader;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.dto.UserDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingRepository ratingRepository;
    private final BookRepository bookRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, RatingRepository ratingRepository,
                       BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.ratingRepository = ratingRepository;
        this.bookRepository = bookRepository;
    }

    public void createUser(UserDTO userDTO) {
        Objects.requireNonNull(userDTO);
        Objects.requireNonNull(userDTO.email());
        Objects.requireNonNull(userDTO.password());
        Objects.requireNonNull(userDTO.name());
        Objects.requireNonNull(userDTO.surname());
        Optional<User> userFromDB = userRepository.findByEmail(userDTO.email());
        if (userFromDB.isPresent()) {
            throw new UserEmailAlreadyExistsException(userDTO.email());
        }
        User user = UserDTO.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setEnabled(true);
        Role role = roleRepository.getByName(SetupDataLoader.USER);
        user.setRole(role);
        userRepository.save(user);
    }

    public void createAdmin(UserDTO adminDTO) {
        Objects.requireNonNull(adminDTO);
        Objects.requireNonNull(adminDTO.email());
        Objects.requireNonNull(adminDTO.password());
        Objects.requireNonNull(adminDTO.name());
        Objects.requireNonNull(adminDTO.surname());
        Optional<User> adminFromDB = userRepository.findByEmail(adminDTO.email());
        if (adminFromDB.isPresent()) {
            throw new UserEmailAlreadyExistsException(adminDTO.email());
        }
        User admin = UserDTO.toUser(adminDTO);
        admin.setPassword(passwordEncoder.encode(adminDTO.password()));
        admin.setEnabled(true);
        Role role = roleRepository.getByName(SetupDataLoader.ADMIN);
        admin.setRole(role);
        userRepository.save(admin);
    }

    public void createEditor(UserDTO editorDTO) {
        Objects.requireNonNull(editorDTO);
        Objects.requireNonNull(editorDTO.email());
        Objects.requireNonNull(editorDTO.password());
        Objects.requireNonNull(editorDTO.name());
        Objects.requireNonNull(editorDTO.surname());
        Optional<User> adminFromDB = userRepository.findByEmail(editorDTO.email());
        if (adminFromDB.isPresent()) {
            throw new UserEmailAlreadyExistsException(editorDTO.email());
        }
        User editor = UserDTO.toUser(editorDTO);
        editor.setPassword(passwordEncoder.encode(editorDTO.password()));
        editor.setEnabled(true);
        Role role = roleRepository.getByName(SetupDataLoader.EDITOR);
        editor.setRole(role);
        userRepository.save(editor);
    }

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
        if (countOfRating == null) {
            countOfRating = 0L;
        }
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

    public Iterable<UserDTO> getUsers(PaginationDTO paginationDTO) {
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<User> users = userRepository.findAll(pageRequest);
        return UserDTO.mapUserListToUserDtoList(users);
    }

    public UserDTO getUserById(Long id) {
        Objects.requireNonNull(id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException(id));
        return UserDTO.fromUser(user);
    }

    public UserDTO getUserByEmail(String email) {
        Objects.requireNonNull(email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserEmailNotFoundException(email));
        return UserDTO.fromUser(user);
    }

    public UserDTO updateUser(Long id) {
        Objects.requireNonNull(id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException(id));
        user.setName("user");
        User changedUser = userRepository.save(user);
        return UserDTO.fromUser(changedUser);
    }

    public void deleteUser(Long id) {
        Objects.requireNonNull(id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else throw new BookListIdNotFoundException(id);
    }
}