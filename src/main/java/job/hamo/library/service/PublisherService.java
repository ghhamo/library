package job.hamo.library.service;

import job.hamo.library.entity.Publisher;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class PublisherService {

    public Map<String, Publisher> publishersMapToMap(Collection<Publisher> list) {
        Map<String, Publisher> publishers = new HashMap<>();
        for (Publisher publisher : list) {
            publishers.put(publisher.getName().toLowerCase(), publisher);
        }
        return publishers;
    }
}
