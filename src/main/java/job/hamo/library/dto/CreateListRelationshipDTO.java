package job.hamo.library.dto;

public record CreateListRelationshipDTO(Long listId, Long bookId) {

    public static CreateListRelationshipDTO makeRelationship(Object listId, Object bookId) {
        return new CreateListRelationshipDTO(
                (Long) listId,
                (Long) bookId);
    }

    public static CreateListRelationshipDTO toCreateListRelationshipDTO(String listId, String bookId) {
        return new CreateListRelationshipDTO(Long.parseLong(listId),Long.parseLong(bookId));
    }
}
