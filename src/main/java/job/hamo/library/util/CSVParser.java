package job.hamo.library.util;

import job.hamo.library.exception.InvalidCSVFormatException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class CSVParser {

    private final String[] bookHeader = new String[]{"id", "title", "big_image_url", "author_id", "genre_id"};
    private final String[] authorHeader = new String[]{"id", "name"};
    private final String[] bookCollectionHeader = new String[]{"id", "name", "user_id"};
    private final String[] collection_booksHeader = new String[]{"collection_id", "book_id"};
    private final String[] bookListHeader = new String[]{"id", "name", "user_id"};
    private final String[] list_booksHeader = new String[]{"list_id", "book_id"};
    private final String[] genreHeader = new String[]{"id", "name"};
    private final String[] ratingHeader = new String[]{"id", "rating", "book_id", "user_id"};
    private final String[] roleHeader = new String[]{"id", "name"};
    private final String[] userHeader = new String[]{"id", "name", "surname", "email", "password", "enabled", "role_name"};


    public List<String[]> csvParseToString(MultipartFile file, String fileName) {
        List<String[]> rows = new ArrayList<>();
        String line;
        String splitBy = ",";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String firstRow = br.readLine();
            if (firstRow == null) {
                throw new InvalidCSVFormatException();
            }
            String[] hed = firstRow.split(splitBy);
            validateHeader(hed, fileName);
            while ((line = br.readLine()) != null) {
                rows.add(line.split(splitBy));
            }
        } catch (IOException e) {
            e.printStackTrace();
            // todo
            throw new RuntimeException(e);
        }
        return rows;
    }

    private void validateHeader(String[] header, String fileName) {
        if (Objects.equals(fileName, "genre")) {
            if (!Arrays.equals(header, genreHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "author")) {
            if (!Arrays.equals(header, authorHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "book")) {
            if (!Arrays.equals(header, bookHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "role")) {
            if (!Arrays.equals(header, roleHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "user")) {
            if (!Arrays.equals(header, userHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "rating")) {
            if (!Arrays.equals(header, ratingHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "bookList")) {
            if (!Arrays.equals(header, bookListHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "bookCollection")) {
            if (!Arrays.equals(header, bookCollectionHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "collectionBooks")) {
            if (!Arrays.equals(header, collection_booksHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else if (Objects.equals(fileName, "listBooks")) {
            if (!Arrays.equals(header, list_booksHeader)) {
                throw new InvalidCSVFormatException();
            }
        } else {
            throw new InvalidCSVFormatException();
        }
    }
}