package job.hamo.library.dto;

public record CreateCollectionRelationshipDTO(Long collectionId, Long bookId) {

    public static CreateCollectionRelationshipDTO makeRelationship(Object collectionId, Object bookId) {
        return new CreateCollectionRelationshipDTO(
                (Long) collectionId, (Long) bookId);
    }

    public static CreateCollectionRelationshipDTO toCreateCollectionRelationshipDTO(String collectionId, String bookId) {
        return new CreateCollectionRelationshipDTO(Long.parseLong(collectionId),Long.parseLong(bookId));
    }
}
