package job.hamo.library.controller;

import job.hamo.library.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class TokenController {

    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public TokenController(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return myUserDetailsService.generateToken(authentication);
    }
}
