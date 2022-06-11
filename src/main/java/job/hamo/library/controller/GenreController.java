package job.hamo.library.controller;

import job.hamo.library.dto.GenreDTO;
import job.hamo.library.dto.PaginationDTO;
import job.hamo.library.service.GenreService;
import job.hamo.library.util.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @Value("${page.max.size}")
    private Integer pageMaxSize;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public Iterable<GenreDTO> getAll(@RequestParam int pageIndex, @RequestParam int pageSize) {
        if (pageMaxSize < pageSize) {
            throw new IllegalStateException();
        }
        return genreService.getAllGenre(new PaginationDTO(pageIndex, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public ResponseEntity<GenreDTO> create(@RequestBody GenreDTO genreDto) {
        return ResponseEntity.ok().body(genreService.create(genreDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public GenreDTO getOne(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN', 'SCOPE_USER')")
    public GenreDTO getByName(@PathVariable String name) {
        return genreService.getGenreByName(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public GenreDTO updateGenre(@PathVariable Long id) {
        return genreService.updateGenre(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public void delete(@PathVariable Long id) {
        genreService.deleteGenre(id);
    }
}
