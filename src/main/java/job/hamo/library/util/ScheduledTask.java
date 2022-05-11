package job.hamo.library.util;

import job.hamo.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    private BookService bookService;

    @Scheduled(fixedRate = 300000)
    public void processImages() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        bookService.downloadBookImages();
        String strDate = sdf.format(now);
        System.err.println("do schedule:: " + strDate);
    }
}
