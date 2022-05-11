package job.hamo.library.dto;

import job.hamo.library.entity.Role;

import java.util.UUID;

public record RoleDTO(UUID id, String name) {

    public static RoleDTO fromRole(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }

    public static Role toRole(RoleDTO roleDTO) {
        Role role = new Role();
        role.setId(roleDTO.id);
        role.setName(roleDTO.name);
        return role;
    }
}
