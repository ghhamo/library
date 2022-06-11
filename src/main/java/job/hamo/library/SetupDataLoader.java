package job.hamo.library;

import job.hamo.library.entity.Privilege;
import job.hamo.library.entity.Role;
import job.hamo.library.entity.User;
import job.hamo.library.repository.PrivilegeRepository;
import job.hamo.library.repository.RoleRepository;
import job.hamo.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;


@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    public static final String USER = "USER";
    public static final String EDITOR = "EDITOR";
    public static final String ADMIN = "ADMIN";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    boolean alreadySetup = false;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository,
                           PrivilegeRepository privilegeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        createRoleIfNotFound(USER, Collections.emptySet());
        createRoleIfNotFound(EDITOR, Collections.emptySet());
        createRoleIfNotFound(ADMIN, Collections.emptySet());
        createRoleIfNotFound(SUPER_ADMIN, Collections.emptySet());
        Role adminRoleFromDB = roleRepository.findByName(SUPER_ADMIN)
                .orElseThrow(IllegalArgumentException::new);
        String email = "admin@superadmin.com";
        Optional<User> adminFromDB = userRepository.findByEmail(email);
        if (adminFromDB.isPresent()) {
            return;
        }
        User admin = new User();
        admin.setId(Long.MAX_VALUE);
        admin.setName("SuperAdmin");
        admin.setSurname("SuperAdmin");
        admin.setEmail(email);
        admin.setEnabled(true);
        admin.setRole(adminRoleFromDB);
        admin.setAge(34);
        admin.setCity("SuperAdmin");
        admin.setRegion("SuperAdmin");
        admin.setCountry("SuperAdmin");
        admin.setPassword(passwordEncoder.encode("superadmin"));
        userRepository.save(admin);
        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = new Privilege();
        Optional<Privilege> privilegeFromDB = privilegeRepository.findByName(name);
        if (privilegeFromDB.isEmpty()) {
            privilege.setName(name);
            privilegeRepository.save(privilege);
            return privilege;
        }
        return privilegeFromDB.get();
    }

    @Transactional
    void createRoleIfNotFound(String name, Set<Privilege> privileges) {
        Role role = new Role();
        Optional<Role> roleFromDB = roleRepository.findByName(name);
        if (roleFromDB.isEmpty()) {
            role.setName(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
}