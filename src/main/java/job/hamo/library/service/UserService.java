package job.hamo.library.service;

import job.hamo.library.SetupDataLoader;
import job.hamo.library.dto.CreateUserDTO;
import job.hamo.library.dto.LocationDTO;
import job.hamo.library.dto.RoleDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.*;
import job.hamo.library.util.CsvParser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JwtEncoder encoder;

    @Autowired
    private LocationService locationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CsvParser csvParser;

    private Role userRole;


    public void importUsers(MultipartFile file) {
        List<String[]> rows = csvParser.csvParseToString(file, "user");
        List<User> users = new ArrayList<>();
        userRole = roleRepository.getByName(SetupDataLoader.ROLE_USER);
        for (String[] row : rows) {
            String[] location = row[1].split(",");
            LocationDTO locationDTO = locationService.stringToLocation(location);
            User user = new User();
            user.setId(Long.parseLong(row[0]));
            user.setCity(locationDTO.city());
            user.setCountry(locationDTO.country());
            user.setRegion(locationDTO.region());
            user.setRole(userRole);
            user.setEnabled(true);
            if (row[2].equals("NULL")) {
                user.setAge(0);
            } else {
                user.setAge(Integer.parseInt(row[2]));
            }
            users.add(user);
        }
        userRepository.saveAll(users);
    }

    @Transactional
    public List<RoleDTO> importRoles(Iterable<RoleDTO> roleDTOS) {
        List<RoleDTO> invalidDTOs = new LinkedList<>();
        for (RoleDTO roleDTO : roleDTOS) {
            if (roleDTO == null) {
                continue;
            }
            try {
                createRole(roleDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(roleDTO);
            }
        }
        return invalidDTOs;
    }

    private void createRole(RoleDTO roleDTO) {
        Objects.requireNonNull(roleDTO);
        Objects.requireNonNull(roleDTO.name());
        if (roleDTO.id() != null) {
            boolean existsById = roleRepository.existsById(roleDTO.id());
            if (existsById) {
                throw new RoleIdAlreadyExistsException(roleDTO.id());
            }
            entityManager.createNativeQuery("INSERT INTO role (id, name) VALUES (?,?)")
                    .setParameter(1, roleDTO.id())
                    .setParameter(2, roleDTO.name())
                    .executeUpdate();
        } else {
            roleRepository.save(RoleDTO.toRole(roleDTO));
        }
    }

    public void createUser(CreateUserDTO createUserDto) {

    }

    public String sign_in(Authentication authentication) {
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
}