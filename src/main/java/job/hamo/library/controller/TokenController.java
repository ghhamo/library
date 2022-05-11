package job.hamo.library.controller;

import job.hamo.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class TokenController {

    @Autowired
    private UserService userService;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return userService.sign_in(authentication);
    }
}
