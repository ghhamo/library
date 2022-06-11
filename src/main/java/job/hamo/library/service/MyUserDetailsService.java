package job.hamo.library.service;

import job.hamo.library.entity.Privilege;
import job.hamo.library.entity.Role;
import job.hamo.library.entity.User;
import job.hamo.library.exception.UserEmailNotFoundException;
import job.hamo.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtEncoder encoder;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository, JwtEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserEmailNotFoundException(email));
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.isEnabled(),
                true, true, true, authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {

        return getGrantedAuthorities(getPrivileges(role));
    }

    private Set<String> getPrivileges(Role role) {
        Set<String> privileges = new HashSet<>();
        privileges.add(role.getName());
        Set<Privilege> collection = new HashSet<>(role.getPrivileges());
        for (Privilege privilege : collection) {
            privileges.add(privilege.getName());
        }
        return privileges;
    }

    private Set<GrantedAuthority> getGrantedAuthorities(Set<String> privileges) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}