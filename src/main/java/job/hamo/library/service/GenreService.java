package job.hamo.library.service;

import job.hamo.library.dto.GenreDTO;
import job.hamo.library.entity.Genre;
import job.hamo.library.exception.*;
import job.hamo.library.repository.GenreRepository;
import job.hamo.library.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

import static job.hamo.library.util.DataGenerator.randomString;


@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<GenreDTO> exportAll() {
        List<Genre> all = genreRepository.findAll();
        List<GenreDTO> result = new LinkedList<>();
        for (Genre genre : all) {
            result.add(GenreDTO.fromGenre(genre));
        }
        return result;
    }

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

    public GenreDTO create(GenreDTO genreDto) {
        Objects.requireNonNull(genreDto);
        Objects.requireNonNull(genreDto.name());
        Genre genre;
        if (genreDto.id() != null) {
            boolean existsById = genreRepository.existsById(genreDto.id());
            if (existsById) {
                throw new GenreUUIDAlreadyExistsException(genreDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO genre (id, name) VALUES (?,?)")
                    .setParameter(1, UUIDUtil.asBytes(genreDto.id()))
                    .setParameter(2, genreDto.name())
                    .executeUpdate();
            genre = genreRepository.getById(genreDto.id());
        } else {
            genre = genreRepository.save(GenreDTO.toGenre(genreDto));
        }

        return GenreDTO.fromGenre(genre);
    }

    public GenreDTO getGenreById(UUID id) {
        Objects.requireNonNull(id);
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreUUIDNotFoundException(id));
        return GenreDTO.fromGenre(genre);
    }

    public GenreDTO updateGenre(UUID id) {
        Objects.requireNonNull(id);
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreUUIDNotFoundException(id));
        genre.setName(randomString(12));
        Genre changedGenre = genreRepository.save(genre);
        return GenreDTO.fromGenre(changedGenre);
    }

    public Genre csvToGenre(String[] genreRow) {
        Genre genre = new Genre();
        genre.setId(UUID.fromString(genreRow[0]));
        genre.setName(genreRow[1]);
        return genre;
    }
}
