package job.hamo.library.dto;

import job.hamo.library.util.UUIDUtil;

import java.util.UUID;

public record CreateCollectionRelationshipDTO(UUID collectionId, UUID bookId) {

    public static CreateCollectionRelationshipDTO makeRelationship(Object collectionId, Object bookId) {
        return new CreateCollectionRelationshipDTO(
                UUIDUtil.asUUID((byte[]) collectionId),
                UUIDUtil.asUUID((byte[]) bookId));
    }

    public static CreateCollectionRelationshipDTO toCreateCollectionRelationshipDTO(String collectionId, String bookId) {
        return new CreateCollectionRelationshipDTO(UUID.fromString(collectionId),UUID.fromString(bookId));
    }
}
