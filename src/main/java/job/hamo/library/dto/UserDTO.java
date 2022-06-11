package job.hamo.library.dto;

import job.hamo.library.entity.User;

import java.util.HashSet;
import java.util.Set;

public record UserDTO(Long id, String name, String surname, String email,
                      String password, String city, String region, String country, int age) {

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getSurname(),
                user.getEmail(), user.getPassword(), user.getCity(),
                user.getRegion(), user.getCountry(), user.getAge());
    }

    public static User toUser(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.name);
        user.setSurname(userDto.surname);
        user.setEmail(userDto.email);
        user.setPassword(userDto.password);
        user.setCity(userDto.city);
        user.setRegion(userDto.region);
        user.setCountry(userDto.country);
        user.setAge(userDto.age);
        return user;
    }

    public static Iterable<UserDTO> mapUserListToUserDtoList(Iterable<User> users) {
        Set<UserDTO> userDTOSet = new HashSet<>();
        for (User user : users) {
            userDTOSet.add(UserDTO.fromUser(user));
        }
        return userDTOSet;
    }

}
