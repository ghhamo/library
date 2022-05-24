package job.hamo.library.dto;

import java.util.Objects;

public record CreateUserDTO(Long id, String location, int age) {

    public static CreateUserDTO toCreateUserDTO(String[] row) {
        if (Objects.equals(row[2], "NULL")) {
            return new CreateUserDTO(Long.parseLong(row[0]), row[1], 0);
        }
        return new CreateUserDTO(Long.parseLong(row[0]), row[1], Integer.parseInt(row[2]));
    }
}
