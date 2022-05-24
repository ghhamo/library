package job.hamo.library.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

//@Component
public class ScheduledTask {

    @Scheduled(fixedRate = 1000)
    public void createDownloadJobs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();

        String strDate = sdf.format(now);
        System.err.println("The download jobs are created:: " + strDate);
    }

    @Scheduled(fixedRate = 1000)
    public void createImageResizingMediumJobs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();

        String strDate = sdf.format(now);
        System.err.println("The ImageResizingMediumJobs jobs are created:: " + strDate);
    }

    @Scheduled(fixedRate = 1000)
    public void createImageResizingSmallJobs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();

        String strDate = sdf.format(now);
        System.err.println("The ImageResizingSmallJobs jobs are created:: " + strDate);
    }

    @Scheduled(fixedRate = 1000)
    public void readJobs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();

        String strDate = sdf.format(now);
        System.err.println("The process is done:: " + strDate);
    }

}
