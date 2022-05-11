package job.hamo.library.dto;

import job.hamo.library.entity.Role;
import job.hamo.library.entity.User;

import java.util.UUID;

public record CreateUserDTO(UUID id, String name, String surname, String email,
                            String password, boolean enabled, String roleName) {


    public static CreateUserDTO fromUser(User user) {
        return new CreateUserDTO(user.getId(), user.getName(), user.getSurname(),
                user.getEmail(), user.getPassword(), user.isEnabled(), user.getRole().getName());
    }


    public static User toUser(CreateUserDTO createUserDTO, Role role) {
        User user = new User();
        user.setName(createUserDTO.name);
        user.setSurname(createUserDTO.surname);
        user.setEmail(createUserDTO.email);
        user.setPassword(createUserDTO.password);
        user.setEnabled(createUserDTO.enabled);
        user.setRole(role);
        return user;
    }
}
