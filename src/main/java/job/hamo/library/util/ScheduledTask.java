package job.hamo.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTask {

    private final ScheduledTaskImpl scheduledTaskImpl;

    @Autowired
    public ScheduledTask(ScheduledTaskImpl scheduledTaskImpl) {
        this.scheduledTaskImpl = scheduledTaskImpl;
    }

    @Scheduled(fixedRate = 1000000)
    public void downloadingImagesScheduledTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        List<Long> list = scheduledTaskImpl.getBookIdsWithPendingBigImages();
        scheduledTaskImpl.downloadingImages(list);
        String strDate = sdf.format(now);
        System.err.println("The downloads are done:: " + strDate);
    }

    @Scheduled(fixedRate = 1000000)
    public void resizingMediumImagesScheduledTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        List<Long> list = scheduledTaskImpl.getBookIdsWithPendingMediumOrSmallImages("medium");
        scheduledTaskImpl.resizingImages(list, "medium");
        String strDate = sdf.format(now);
        System.err.println("The medium resizes are done:: " + strDate);
    }

    @Scheduled(fixedRate = 1000000)
    public void resizingSmallImagesScheduledTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        List<Long> list = scheduledTaskImpl.getBookIdsWithPendingMediumOrSmallImages("small");
        scheduledTaskImpl.resizingImages(list, "small");
        String strDate = sdf.format(now);
        System.err.println("The small resizes are done:: " + strDate);
    }

    @Scheduled(fixedRate = 1000000)
    public void updateInProgressImagesByDateScheduledTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        scheduledTaskImpl.getInProgressImages();
        String strDate = sdf.format(now);
        System.err.println("The updates are done:: " + strDate);
    }
}
