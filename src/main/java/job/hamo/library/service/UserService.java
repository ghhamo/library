package job.hamo.library.service;

import job.hamo.library.SetupDataLoader;
import job.hamo.library.dto.CreateUserDTO;
import job.hamo.library.dto.RoleDTO;
import job.hamo.library.dto.UserDTO;
import job.hamo.library.entity.*;
import job.hamo.library.exception.*;
import job.hamo.library.repository.*;
import job.hamo.library.util.UUIDUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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

    @Transactional
    public List<CreateUserDTO> exportAll() {
        List<User> all = userRepository.findAll();
        List<CreateUserDTO> result = new LinkedList<>();
        for (User user : all) {
            result.add(CreateUserDTO.fromUser(user));
        }
        return result;
    }

    @Transactional
    public List<RoleDTO> exportAllRoles() {
        List<Role> all = roleRepository.findAll();
        List<RoleDTO> result = new LinkedList<>();
        for (Role role : all) {
            result.add(RoleDTO.fromRole(role));
        }
        return result;
    }

    @Transactional
    public List<CreateUserDTO> importUsers(List<CreateUserDTO> userDTOS) {
        List<CreateUserDTO> invalidDTOs = new LinkedList<>();
        for (CreateUserDTO userDTO : userDTOS) {
            if (userDTO == null) {
                continue;
            }
            try {
                createUser(userDTO);
            } catch (ValidationException validationException) {
                invalidDTOs.add(userDTO);
            }
        }
        return invalidDTOs;
    }

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
                throw new RoleUUIDAlreadyExistsException(roleDTO.id());
            }
            entityManager.createNativeQuery("INSERT INTO role (id, name) VALUES (?,?)")
                    .setParameter(1, UUIDUtil.asBytes(roleDTO.id()))
                    .setParameter(2, roleDTO.name())
                    .executeUpdate();
        } else {
            roleRepository.save(RoleDTO.toRole(roleDTO));
        }
    }

    public void createUser(CreateUserDTO createUserDto) {
        validateUser(createUserDto);
        Optional<User> userFromDB = userRepository.findByEmail(createUserDto.email());
        if (userFromDB.isPresent()) {
            throw new UserWithEmailAlreadyExistsException();
        }
        Role role = roleRepository.findByName(createUserDto.roleName()).orElseThrow(() ->
                new RoleNameNotFoundException(createUserDto.roleName()));
        if (createUserDto.id() != null) {
            boolean existsById = roleRepository.existsById(createUserDto.id());
            if (existsById) {
                throw new UserUUIDAlreadyExistsException(createUserDto.id());
            }
            entityManager.createNativeQuery("INSERT INTO user (id, name, surname, email, password, enabled, role_id) VALUES (?,?,?,?,?,?,?)")
                    .setParameter(1, UUIDUtil.asBytes(createUserDto.id()))
                    .setParameter(2, createUserDto.name())
                    .setParameter(3, createUserDto.surname())
                    .setParameter(4, createUserDto.email())
                    .setParameter(5, createUserDto.password())
                    .setParameter(6, createUserDto.enabled())
                    .setParameter(7, UUIDUtil.asBytes(role.getId()))
                    .executeUpdate();
        } else {
            userRepository.save(CreateUserDTO.toUser(createUserDto, role));
        }
    }

    private void validateUser(CreateUserDTO userDto) {
        Objects.requireNonNull(userDto);
        Objects.requireNonNull(userDto.name());
        Objects.requireNonNull(userDto.surname());
        Objects.requireNonNull(userDto.email());
        Objects.requireNonNull(userDto.password());
    }

    public void registerNewEditorAccount(CreateUserDTO editorDto) {
        validateUser(editorDto);
        Optional<User> userFromDB = userRepository.findByEmail(editorDto.email());
        if (userFromDB.isPresent()) {
            throw new UserWithEmailAlreadyExistsException();
        }
        Role role = roleRepository.getByName(SetupDataLoader.ROLE_EDITOR);
        User editor = CreateUserDTO.toUser(editorDto, role);
        editor.setRole(role);
        userRepository.save(editor);
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

    public void createNewAdmin(CreateUserDTO newAdmin, UUID adminId) {
        Objects.requireNonNull(adminId);
        validateUser(newAdmin);
        User oldAdmin = userRepository.findById(adminId).orElseThrow(() ->
                new UserUUIDNotFoundException(adminId));
        if (!oldAdmin.getRole().getName().equals(SetupDataLoader.ROLE_ADMIN)) {
            throw new NotPermissionException();
        }
        Role role = roleRepository.getByName(SetupDataLoader.ROLE_ADMIN);
        User admin = CreateUserDTO.toUser(newAdmin, role);
        admin.setRole(role);
        userRepository.save(admin);
    }

    public Role csvToRole(String[] roleRow) {
        Role role = new Role();
        role.setId(UUID.fromString(roleRow[0]));
        role.setName(roleRow[1]);
        return role;
    }

    @Transactional
    public User csvToUser(String[] userRow) {
        User user = new User();
        user.setId(UUID.fromString(userRow[0]));
        user.setEmail(userRow[1]);
        user.setEnabled(true);
        user.setName(userRow[3]);
        user.setPassword(userRow[4]);
        user.setSurname(userRow[5]);
        Role role = roleRepository.getByName(userRow[6]);
        user.setRole(role);
        return user;
    }
}