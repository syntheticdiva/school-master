package school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;
import school.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final ThreadService threadService;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository,
                             ThreadService threadService) {
        this.subscriberRepository = subscriberRepository;
        this.threadService = threadService;
    }

    public SubscriberEntity createSubscriber(SubscriberDto subscriberDto) {
        SubscriberEntity entity = new SubscriberEntity();
        entity.setEntity(
                Optional.ofNullable(subscriberDto.getEntity())
                        .orElse(SubscriberDto.ENTITY_SCHOOL)
        );
        entity.setEventType(
                Optional.ofNullable(subscriberDto.getEventType())
                        .orElse(SubscriberDto.EVENT_ON_CREATE)
        );
        entity.setUrl(subscriberDto.getUrl());
        entity.setCreatedAt(LocalDateTime.now());

        return subscriberRepository.save(entity);
    }
}