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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;


@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_EDITOR = "ROLE_EDITOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    boolean alreadySetup = false;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        createRoleIfNotFound(ROLE_USER, Collections.emptySet());
        createRoleIfNotFound(ROLE_EDITOR, Collections.emptySet());
        createRoleIfNotFound(ROLE_ADMIN, Collections.emptySet());

        Role adminRoleFromDB = roleRepository.findByName(ROLE_ADMIN).orElseThrow(IllegalArgumentException::new);

        String email = "admin@admin.com";
        Optional<User> adminFromDB = userRepository.findByEmail(email);
        if (adminFromDB.isPresent()) {
            return;
        }
        String sql = "INSERT INTO user (id, name, surname, email, password, enabled, role_id, age, city, region, country) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String name = "Admin";
        String surname = "Admin";
        String city = "Admin";
        String region = "Admin";
        String country = "Admin";
        String password = passwordEncoder.encode("admin");
        entityManager.createNativeQuery(sql)
                .setParameter(1, Long.MAX_VALUE)
                .setParameter(2, name)
                .setParameter(3, surname)
                .setParameter(4, email)
                .setParameter(5, password)
                .setParameter(6, true)
                .setParameter(7, adminRoleFromDB.getId())
                .setParameter(8, 432)
                .setParameter(9, city)
                .setParameter(10, region)
                .setParameter(11, country)
                .executeUpdate();
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