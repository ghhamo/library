package job.hamo.library.service;

import job.hamo.library.dto.GenreDTO;
import job.hamo.library.entity.Genre;
import job.hamo.library.exception.*;
import job.hamo.library.repository.GenreRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<GenreDTO> importGenres(Iterable<GenreDTO> genreDTOS) {
        List<GenreDTO> invalidDTOs = new LinkedList<>();
        for (GenreDTO genreDTO : genreDTOS) {
            if (genreDTO == null) {
                continue;
            }
            try {
                create(genreDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(genreDTO);
            }
        }
        return invalidDTOs;
    }


    public Iterable<GenreDTO> getAllGenre() {
        Iterable<Genre> genres = genreRepository.findAll();
        return GenreDTO.mapGenreListToGenreDtoList(genres);
    }

    @Transactional
    public GenreDTO create(GenreDTO genreDto) {
        Objects.requireNonNull(genreDto);
        Objects.requireNonNull(genreDto.name());
        Genre genre;
        if (genreDto.id() != null) {
            boolean existsById = genreRepository.existsById(genreDto.id());
            if (existsById) {
                throw new GenreIdAlreadyExistsException(genreDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO genre (id, name) VALUES (?,?)")
                    .setParameter(1, genreDto.id())
                    .setParameter(2, genreDto.name())
                    .executeUpdate();
            genre = genreRepository.getById(genreDto.id());
        } else {
            genre = genreRepository.save(GenreDTO.toGenre(genreDto));
        }

        return GenreDTO.fromGenre(genre);
    }

    public GenreDTO getGenreById(Long id) {
        Objects.requireNonNull(id);
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreIdNotFoundException(id));
        return GenreDTO.fromGenre(genre);
    }

    public GenreDTO updateGenre(Long id) {
        Objects.requireNonNull(id);
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreIdNotFoundException(id));
        genre.setName("genre");
        Genre changedGenre = genreRepository.save(genre);
        return GenreDTO.fromGenre(changedGenre);
    }
}
