package job.hamo.library.dto;

import job.hamo.library.util.UUIDUtil;

import java.util.UUID;

public record CreateListRelationshipDTO(UUID listId, UUID bookId) {

    public static CreateListRelationshipDTO makeRelationship(Object listId, Object bookId) {
        return new CreateListRelationshipDTO(
                UUIDUtil.asUUID((byte[]) listId),
                UUIDUtil.asUUID((byte[]) bookId));
    }

    public static CreateListRelationshipDTO toCreateListRelationshipDTO(String listId, String bookId) {
        return new CreateListRelationshipDTO(UUID.fromString(listId),UUID.fromString(bookId));
    }
}
