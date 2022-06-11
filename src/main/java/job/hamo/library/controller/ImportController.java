package job.hamo.library.controller;

import job.hamo.library.util.ImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csvs")
public class ImportController {

    private final ImportUtil importUtil;

    @Autowired
    public ImportController(ImportUtil importUtil) {
        this.importUtil = importUtil;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN')")
    public void importAll(@RequestParam("booksFile") MultipartFile booksFile,
                          @RequestParam("usersFile") MultipartFile usersFile,
                          @RequestParam("ratingsFile") MultipartFile ratingsFile) {
        importUtil.importAll(booksFile, usersFile, ratingsFile);
    }
}
