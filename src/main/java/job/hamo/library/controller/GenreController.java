package job.hamo.library.controller;

import job.hamo.library.dto.GenreDTO;
import job.hamo.library.entity.Genre;
import job.hamo.library.service.GenreService;
import job.hamo.library.util.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @Autowired
    private CSVParser csvParser;

    @GetMapping
    public Iterable<GenreDTO> getAll() {
        return genreService.getAllGenre();
    }

    @PostMapping
    public ResponseEntity<GenreDTO> create(@RequestBody GenreDTO genreDto) {
        return ResponseEntity.ok().body(genreService.create(genreDto));
    }

    @GetMapping("/export")
    public ResponseEntity<String> export() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<GenreDTO> genreDTOS = genreService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name");
        for (GenreDTO genreDTO : genreDTOS) {
            csvBuilder.append("\n")
                    .append(genreDTO.id())
                    .append(',')
                    .append(genreDTO.name());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("/import")
    public Iterable<GenreDTO> importGenre(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "genre");
        List<GenreDTO> genreDTOS = new ArrayList<>();
        for (String[] row : rows) {
            Genre genre = genreService.csvToGenre(row);
            genreDTOS.add(GenreDTO.fromGenre(genre));
        }
        return genreService.importGenres(genreDTOS);
    }

    @GetMapping("/{id}")
    public GenreDTO getOne(@PathVariable UUID id) {
        return genreService.getGenreById(id);
    }


    @PutMapping("/{id}")
    public GenreDTO updateGenre(@PathVariable UUID id) {
        return genreService.updateGenre(id);
    }
}
