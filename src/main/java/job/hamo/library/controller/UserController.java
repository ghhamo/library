package job.hamo.library.controller;

import job.hamo.library.dto.CreateUserDTO;
import job.hamo.library.dto.RoleDTO;
import job.hamo.library.entity.Role;
import job.hamo.library.service.UserService;
import job.hamo.library.util.CSVUtil;
import job.hamo.library.util.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CsvParser csvParser;

    @Autowired
    private CSVUtil csvUtil;

    @PostMapping("import/roles")
    public Iterable<RoleDTO> importRoles(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "role");
        List<RoleDTO> roleDTOS = new ArrayList<>();
        for (String[] row : rows) {
            Role role = csvUtil.csvToRole(row);
            roleDTOS.add(RoleDTO.fromRole(role));
        }
        return userService.importRoles(roleDTOS);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void importUsers(@RequestParam("file") MultipartFile file) {
         userService.importUsers(file);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody CreateUserDTO user) {
        userService.createUser(user);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }
}