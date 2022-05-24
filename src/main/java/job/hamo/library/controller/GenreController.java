package job.hamo.library.controller;

import job.hamo.library.dto.GenreDTO;
import job.hamo.library.entity.Genre;
import job.hamo.library.service.GenreService;
import job.hamo.library.util.CsvParser;
import job.hamo.library.util.CSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private CSVUtil csvUtil;

    @GetMapping
    public Iterable<GenreDTO> getAll() {
        return genreService.getAllGenre();
    }

    @PostMapping
    public ResponseEntity<GenreDTO> create(@RequestBody GenreDTO genreDto) {
        return ResponseEntity.ok().body(genreService.create(genreDto));
    }

    @PostMapping("/import")
    public Iterable<GenreDTO> importGenre(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "genre");
        List<GenreDTO> genreDTOS = new ArrayList<>();
        for (String[] row : rows) {
            Genre genre = csvUtil.csvToGenre(row);
            genreDTOS.add(GenreDTO.fromGenre(genre));
        }
        return genreService.importGenres(genreDTOS);
    }

    @GetMapping("/{id}")
    public GenreDTO getOne(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }


    @PutMapping("/{id}")
    public GenreDTO updateGenre(@PathVariable Long id) {
        return genreService.updateGenre(id);
    }
}
