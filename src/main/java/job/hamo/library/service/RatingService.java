package job.hamo.library.service;

import job.hamo.library.dto.CreateRatingDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.BookRepository;
import job.hamo.library.repository.RatingRepository;
import job.hamo.library.repository.UserRepository;
import job.hamo.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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
    private EntityManager entityManager;


    @Transactional
    public List<CreateRatingDTO> exportAll() {
        List<Rating> all = ratingRepository.findAll();
        List<CreateRatingDTO> result = new LinkedList<>();
        for (Rating rating : all) {
            result.add(CreateRatingDTO.fromRating(rating));
        }
        return result;
    }

    @Transactional
    public List<CreateRatingDTO> importRatings(Iterable<CreateRatingDTO> ratingDTOS) {
        List<CreateRatingDTO> invalidDTOs = new LinkedList<>();
        for (CreateRatingDTO createRatingDTO : ratingDTOS) {
            if (createRatingDTO == null) {
                continue;
            }
            try {
                create(createRatingDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(createRatingDTO);
            }
        }
        return invalidDTOs;
    }

    public CreateRatingDTO create(CreateRatingDTO createRatingDTO) {
        Objects.requireNonNull(createRatingDTO);
        Objects.requireNonNull(createRatingDTO.bookId());
        Objects.requireNonNull(createRatingDTO.userId());
        User user = userRepository.findById(createRatingDTO.userId()).orElseThrow(() ->
                new UserUUIDNotFoundException(createRatingDTO.userId()));
        Book book = bookRepository.findById(createRatingDTO.bookId()).orElseThrow(() ->
                new BookUUIDNotFoundException(createRatingDTO.bookId()));
        Rating rating;
        if (createRatingDTO.id() != null) {
            boolean existsById = ratingRepository.existsById(createRatingDTO.id());
            if (existsById) {
                throw new RatingUUIDAlreadyExistsException(createRatingDTO.id());
            }
            entityManager.createNativeQuery("INSERT INTO rating (id, rating, book_id, user_id) VALUES (?,?,?,?)")
                    .setParameter(1, UUIDUtil.asBytes(createRatingDTO.id()))
                    .setParameter(2, createRatingDTO.rating())
                    .setParameter(3, UUIDUtil.asBytes(book.getId()))
                    .setParameter(4, UUIDUtil.asBytes(user.getId()))
                    .executeUpdate();
            rating = ratingRepository.getById(createRatingDTO.id());
        } else {
            rating = ratingRepository.save(CreateRatingDTO.toRating(createRatingDTO));
        }
        return CreateRatingDTO.fromRating(rating);
    }


    public Rating csvToRating(String[] ratingRow) {
        Rating rating = new Rating();
        rating.setId(UUID.fromString(ratingRow[0]));
        rating.setRating(Integer.parseInt(ratingRow[1]));
        Book book = new Book();
        book.setId(UUID.fromString(ratingRow[2]));
        rating.setBook(book);
        User user = new User();
        user.setId(UUID.fromString(ratingRow[3]));
        rating.setUser(user);
        return rating;
    }
}
