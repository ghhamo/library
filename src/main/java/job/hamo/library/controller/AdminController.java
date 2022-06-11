package job.hamo.library.controller;

import job.hamo.library.dto.UserDTO;
import job.hamo.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<HttpStatus> create(@RequestBody UserDTO admin) {
        userService.createAdmin(admin);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }
}
