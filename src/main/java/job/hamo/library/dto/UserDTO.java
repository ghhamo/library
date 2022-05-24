package job.hamo.library.dto;

import job.hamo.library.entity.Role;
import job.hamo.library.entity.User;

import java.util.UUID;

public record UserDTO(Long id, String name, String surname, String email,
                      String password, boolean enabled, Long roleId) {

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getSurname(),
                user.getEmail(), user.getPassword(), user.isEnabled(), user.getRole().getId());
    }

    public static User toUser(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.name);
        user.setSurname(userDto.surname);
        user.setEmail(userDto.email);
        user.setPassword(userDto.password);
        user.setEnabled(userDto.enabled);
        Role role = new Role();
        role.setId(userDto.roleId);
        user.setRole(role);
        return user;
    }

}
