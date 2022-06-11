package job.hamo.library.util;

import job.hamo.library.exception.InvalidCSVFormatException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Component
public class CsvParser {

    private final String[] bookHeader = new String[]{"ISBN", "Book-Title", "Book-Author", "Year-Of-Publication", "Publisher", "Image-URL-S", "Image-URL-M", "Image-URL-L"};
    private final String[] ratingHeader = new String[]{"User-ID", "ISBN", "Book-Rating"};
    private final String[] userHeader = new String[]{"User-ID", "Location", "Age"};


    public List<String[]> csvParseToString(MultipartFile file, String fileName) {
        List<String[]> rows = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CSVParser csvParser = CSVParser.parse(br, CSVFormat.DEFAULT.withDelimiter(';').withQuote('"').withEscape('\\').withFirstRecordAsHeader());
            String[] header = csvParser.getHeaderNames().toArray(new String[0]);
            validateHeader(header, fileName);
            int rowSize = header.length;
            for (CSVRecord csvRecord : csvParser) {
                String[] row = new String[rowSize];
                for (int i = 0; i < row.length; i++) {
                    row[i] = csvRecord.get(i);
                }
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // todo
            throw new RuntimeException(e);
        }
        return rows;
    }

    private void validateHeader(String[] header, String fileName) {
        if (Objects.equals(fileName, "book")) {
            if (!Arrays.equals(header, bookHeader)) {
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
        } else {
            throw new InvalidCSVFormatException();
        }
    }
}