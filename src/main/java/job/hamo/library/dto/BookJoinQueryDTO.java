package job.hamo.library.dto;

import java.util.ArrayList;
import java.util.List;

public record BookJoinQueryDTO(String isbn, String title, String authorName, String publisherName, String bookAssetPath, int bookRating) {

    public static List<BookJoinQueryDTO> objectToBookJoinQueryDTO(List<Object[]> results) {
        List<BookJoinQueryDTO> bookJoinQueryDTOS = new ArrayList<>(results.size());
        for (Object[] result : results) {
            bookJoinQueryDTOS.add(new BookJoinQueryDTO((String) result[0], (String) result[1],
                    (String) result[2], (String) result[3], (String) result[4], (int) result[5]));
        }
        return bookJoinQueryDTOS;
    }
}
