package job.hamo.library.service;

import job.hamo.library.dto.GenreDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.entity.Genre;
import job.hamo.library.exception.*;
import job.hamo.library.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Iterable<GenreDTO> getAllGenre(PaginationDTO paginationDTO) {
        PageRequest pageRequest = PageRequest.of(paginationDTO.pageNumber(), paginationDTO.pageSize());
        Page<Genre> genres = genreRepository.findAll(pageRequest);
        return GenreDTO.mapGenreListToGenreDtoList(genres);
    }

    public GenreDTO create(GenreDTO genreDto) {
        Objects.requireNonNull(genreDto);
        Objects.requireNonNull(genreDto.name());
        Optional<Genre> genre = genreRepository.findByName(genreDto.name());
        if (genre.isPresent()) {
            throw new GenreNameAlreadyExistsException(genreDto.name());
        }
        Genre savedGenre = genreRepository.save(GenreDTO.toGenre(genreDto));
        return GenreDTO.fromGenre(savedGenre);
    }

    public GenreDTO getGenreById(Long id) {
        Objects.requireNonNull(id);
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreIdNotFoundException(id));
        return GenreDTO.fromGenre(genre);
    }

    public GenreDTO getGenreByName(String name) {
        Objects.requireNonNull(name);
        Genre genre = genreRepository.findByName(name).orElseThrow(() -> new GenreNameNotFoundException(name));
        return GenreDTO.fromGenre(genre);
    }

    public GenreDTO updateGenre(Long id) {
        Objects.requireNonNull(id);
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new GenreIdNotFoundException(id));
        genre.setName("genre");
        Genre changedGenre = genreRepository.save(genre);
        return GenreDTO.fromGenre(changedGenre);
    }

    public void deleteGenre(Long id) {
        Objects.requireNonNull(id);
        if (genreRepository.existsById(id)) {
            genreRepository.deleteById(id);
        } else throw new BookListIdNotFoundException(id);
    }
}

