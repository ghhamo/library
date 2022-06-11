package job.hamo.library.util;

import job.hamo.library.exception.DownloadFailedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class BookImageUrlGenerator {


    @Transactional
    public String addBookUrl() {
        String location = "";
        try {
            URL url = new URL("https://picsum.photos/1920/1080");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            location = connection.getHeaderField("location");
            connection.disconnect();
        } catch (Exception exception) {
            throw new DownloadFailedException();
        }
        return location;
    }
}
