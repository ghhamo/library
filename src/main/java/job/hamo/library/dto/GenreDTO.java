package job.hamo.library.dto;

import job.hamo.library.entity.Genre;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record GenreDTO(Long id, String name) {

    public static GenreDTO fromGenre(Genre genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }

    public static Genre toGenre(GenreDTO genreDTO) {
        Genre genre = new Genre();
        genre.setName(genreDTO.name);
        genre.setId(genreDTO.id);
        return genre;
    }

    public static Iterable<GenreDTO> mapGenreListToGenreDtoList(Iterable<Genre> genres) {
        Set<GenreDTO> genreDTOSet = new HashSet<>();
        for (Genre genre : genres) {
            genreDTOSet.add(GenreDTO.fromGenre(genre));
        }
        return genreDTOSet;
    }
}
