package job.hamo.library.dto;

import job.hamo.library.entity.Publisher;

import java.util.HashSet;
import java.util.Set;

public record PublisherDTO(Long id, String name) {
    public static PublisherDTO fromPublisher(Publisher publisher) {
        return new PublisherDTO(publisher.getId(), publisher.getName());
    }

    public static Publisher toPublisher(PublisherDTO publisherDTO) {
        Publisher publisher = new Publisher();
        publisher.setName(publisherDTO.name);
        publisher.setId(publisherDTO.id);
        return publisher;
    }

    public static Iterable<PublisherDTO> mapPublisherListToPublisherDtoList(Iterable<Publisher> publishers) {
        Set<PublisherDTO> publisherDTOSet = new HashSet<>();
        for (Publisher publisher : publishers) {
            publisherDTOSet.add(PublisherDTO.fromPublisher(publisher));
        }
        return publisherDTOSet;
    }
}
