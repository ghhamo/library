package job.hamo.library.service;

import job.hamo.library.dto.CreateRatingDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.repository.RatingRepository;
import job.hamo.library.repository.UserRepository;
import job.hamo.library.util.CsvParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class RatingService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private CsvParser csvParser;

    /*
    public void importRatings(MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "rating");
        List<Rating> ratings = new ArrayList<>();
        Long userMaxId = userRepository.findMaxId();
        for (String[] row : rows) {
            Long userId = Long.parseLong(row[0]);
            if (userId > userMaxId) {
                continue;
            }
            Rating rating = new Rating();
            User user = new User();
            user.setId(userId);
            rating.setUser(user);
            Book book = new Book();
            book.setIsbn(row[1]);
            rating.setBook(book);
            rating.setRating(Integer.parseInt(row[2]));
            ratings.add(rating);
        }
        ratingRepository.saveAll(ratings);
    }*/

    public CreateRatingDTO create(CreateRatingDTO createRatingDTO) {
        Objects.requireNonNull(createRatingDTO);
        Objects.requireNonNull(createRatingDTO.getIsbn());
        Objects.requireNonNull(createRatingDTO.getUserId());
        User user = userRepository.findById(createRatingDTO.getId()).orElseThrow(() ->
                new UserIdNotFoundException(createRatingDTO.getId()));
        Book book = bookRepository.findByIsbn(createRatingDTO.getIsbn()).orElseThrow(() ->
                new BookISBNNotFoundException(createRatingDTO.getIsbn()));
        Rating rating;
        if (createRatingDTO.getId() != null) {
            boolean existsById = ratingRepository.existsById(createRatingDTO.getId());
            if (existsById) {
                throw new RatingIdAlreadyExistsException(createRatingDTO.getId());
            }
            rating = ratingRepository.getById(createRatingDTO.getId());
        } else {
            rating = ratingRepository.save(CreateRatingDTO.toRating(createRatingDTO, book, user));
        }
        return CreateRatingDTO.fromRating(rating);
    }
}
