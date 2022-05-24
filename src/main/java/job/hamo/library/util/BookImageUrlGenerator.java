package job.hamo.library.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class BookImageUrlGenerator {


    @Transactional
    public String addBookUrl() throws IOException {
        URL url = new URL("https://picsum.photos/1920/1080");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("GET");
        String location = connection.getHeaderField("location");
        connection.disconnect();
        return location;
    }
}
