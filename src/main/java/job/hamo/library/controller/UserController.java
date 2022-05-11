package job.hamo.library.controller;

import job.hamo.library.dto.CreateUserDTO;
import job.hamo.library.dto.RoleDTO;
import job.hamo.library.entity.Role;
import job.hamo.library.entity.User;
import job.hamo.library.service.UserService;
import job.hamo.library.util.DataGenerator;
import job.hamo.library.util.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private DataGenerator dataGenerator;

    @Autowired
    private CSVParser csvParser;

    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody UserDto editor) {
        userService.registerNewEditorAccount(editor);
    }*/

    /* @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAdmin(@RequestBody User newAdmin, @PathVariable Long admin_id) {
        userService.createNewAdmin(newAdmin, admin_id);
    }*/

    @PostMapping("/data")
    public ResponseEntity<HttpStatus> createRandomData() {
        dataGenerator.generateRandomData();
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportRoles() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<CreateUserDTO> userDTOS = userService.exportAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name,surname,email,password,enabled,role_name");
        for (CreateUserDTO userDTO: userDTOS) {
            csvBuilder.append("\n")
                    .append(userDTO.id())
                    .append(',')
                    .append(userDTO.name())
                    .append(',')
                    .append(userDTO.surname())
                    .append(',')
                    .append(userDTO.email())
                    .append(',')
                    .append(userDTO.password())
                    .append(',')
                    .append(userDTO.enabled())
                    .append(',')
                    .append(userDTO.roleName());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @GetMapping("/export/roles")
    public ResponseEntity<String> export() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
        List<RoleDTO> roleDTOS = userService.exportAllRoles();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name");
        for (RoleDTO roleDTO: roleDTOS) {
            csvBuilder.append("\n")
                    .append(roleDTO.id())
                    .append(',')
                    .append(roleDTO.name());
        }
        return new ResponseEntity<>(csvBuilder.toString(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping("import/roles")
    public Iterable<RoleDTO> importRoles(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "role");
        List<RoleDTO> roleDTOS = new ArrayList<>();
        for (String[] row : rows) {
            Role role = userService.csvToRole(row);
            roleDTOS.add(RoleDTO.fromRole(role));
        }
        return userService.importRoles(roleDTOS);
    }

    @PostMapping("/import")
    public Iterable<CreateUserDTO> importUsers(@RequestParam("file") MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "user");
        List<CreateUserDTO> userDTOS = new ArrayList<>();
        for (String[] row : rows) {
            User user = userService.csvToUser(row);
            userDTOS.add(CreateUserDTO.fromUser(user));
        }
        return userService.importUsers(userDTOS);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody CreateUserDTO user) {
        userService.createUser(user);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }
}