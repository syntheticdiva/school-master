package school.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;
import school.mapper.SubscriberMapper;
import school.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final ThreadService threadService;
    private final SubscriberMapper  subscriberMapper;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository,
                             ThreadService threadService, SubscriberMapper subscriberMapper ) {
        this.subscriberRepository = subscriberRepository;
        this.threadService = threadService;
        this.subscriberMapper = subscriberMapper;
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
    public SubscriberEntity updateSubscriber(Long id, SubscriberDto subscriberDto) {
        SubscriberEntity existingEntity = subscriberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscriber not found with id: " + id));

        existingEntity.setEntity(
                Optional.ofNullable(subscriberDto.getEntity())
                        .orElse(existingEntity.getEntity())
        );
        existingEntity.setEventType(
                Optional.ofNullable(subscriberDto.getEventType())
                        .orElse(existingEntity.getEventType())
        );
        existingEntity.setUrl(
                Optional.ofNullable(subscriberDto.getUrl())
                        .orElse(existingEntity.getUrl())
        );

        return subscriberRepository.save(existingEntity);
    }

    public void deleteSubscriber(Long id) {
        SubscriberEntity existingEntity = subscriberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscriber not found with id: " + id));

        subscriberRepository.delete(existingEntity);
        threadService.removeSubscriber(id);
    }
    public List<SubscriberDto> getAllSubscribers() {
        List<SubscriberEntity> subscribers = subscriberRepository.findAll();
        return subscribers.stream()
                .map(subscriberMapper::toDto)
                .collect(Collectors.toList());
    }
}