package job.hamo.library.service;

import job.hamo.library.dto.CreateRatingDTO;
import job.hamo.library.dto.RatingDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.repository.RatingRepository;
import job.hamo.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(BookRepository bookRepository, UserRepository userRepository,
                         RatingRepository ratingRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    public CreateRatingDTO create(CreateRatingDTO createRatingDTO) {
        Objects.requireNonNull(createRatingDTO);
        Objects.requireNonNull(createRatingDTO.getIsbn());
        Objects.requireNonNull(createRatingDTO.getUserId());
        User user = userRepository.findById(createRatingDTO.getUserId()).orElseThrow(() ->
                new UserIdNotFoundException(createRatingDTO.getUserId()));
        Book book = bookRepository.findByIsbn(createRatingDTO.getIsbn()).orElseThrow(() ->
                new BookISBNNotFoundException(createRatingDTO.getIsbn()));

        Rating rating = new Rating();
        rating.setRating(createRatingDTO.getRating());
        rating.setBook(book);
        rating.setUser(user);
        Rating savedRating = ratingRepository.save(rating);
        return CreateRatingDTO.fromRating(savedRating);
    }

    public Iterable<RatingDTO> getRatingsOfBook(Long id) {
        Objects.requireNonNull(id);
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookIdNotFoundException(id));
        return RatingDTO.mapRatingSetToRatingDto(book.getRatings());
    }
}
