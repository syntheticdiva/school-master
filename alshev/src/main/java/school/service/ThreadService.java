package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;
import school.exception.SubscriberNotFoundException;
import school.exception.ThreadServiceException;
import school.mapper.SubscriberMapper;
import school.repository.NotificationStatusRepository;
import school.repository.SubscriberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ThreadService {
    private SchoolNotificationThread schoolNotificationThread = null;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;
    private final SchoolNotificationSender notificationSender;
    private final NotificationStatusRepository notificationStatusRepository;
    private final NotificationStatusService notificationStatusService;

    @Autowired
    public ThreadService(
            SubscriberRepository subscriberRepository,
            SubscriberMapper subscriberMapper,
            SchoolNotificationSender notificationSender,
            NotificationStatusRepository notificationStatusRepository,
            NotificationStatusService notificationStatusService
    ) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
        this.notificationSender = notificationSender;
        this.notificationStatusRepository = notificationStatusRepository;
        this.notificationStatusService = notificationStatusService;
    }
    private void checkAndStart() {
        if (schoolNotificationThread == null) {
            schoolNotificationThread = new SchoolNotificationThread(
                    notificationSender,
                    notificationStatusService
            );
            List<SubscriberDto> subscribers = getSubscribers();

            for (SubscriberDto subscriber : subscribers) {
                if (SubscriberDto.EVENT_ON_CREATE.equals(subscriber.getEventType()) ||
                        SubscriberDto.EVENT_ON_UPDATE.equals(subscriber.getEventType()) ||
                        SubscriberDto.EVENT_ON_DELETE.equals(subscriber.getEventType())) {
                    schoolNotificationThread.addSubscriber(subscriber);
                }
            }

            schoolNotificationThread.start();
        }
    }

    public void addSubscriber(SubscriberDto subscriberDto) {
        checkAndStart();
        try {
            schoolNotificationThread.addSubscriber(subscriberDto);
        } catch (RuntimeException e) {
            throw new ThreadServiceException("Error adding subscriber", e);
        }
    }

    public void removeSubscriber(Long subscriberId) {
        checkAndStart();
        try {
            schoolNotificationThread.removeSubscriber(subscriberId);
        } catch (SubscriberNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ThreadServiceException("Error removing subscriber", e);
        }
    }

    public List<SubscriberDto> getSubscribers() {
        List<SubscriberEntity> subscriberEntities = subscriberRepository.findByEntity(SubscriberDto.ENTITY_SCHOOL);

        return subscriberEntities.stream()
                .map(subscriberMapper::toDto)
                .collect(Collectors.toList());
    }

public void addSchoolCreated(SchoolEntityDTO schoolEntityDTO, SubscriberDto subscriber) {
    checkAndStart();
    schoolNotificationThread.addSchoolCreated(schoolEntityDTO);
}
    public void addSchoolUpdated(SchoolUpdateDto schoolUpdateDto) {
        checkAndStart();
        schoolNotificationThread.addSchoolUpdated(schoolUpdateDto);
    }

    public void addSchoolDeleted(SchoolEntityDTO schoolEntityDTO) {
        checkAndStart();
        schoolNotificationThread.addSchoolDeleted(schoolEntityDTO);
    }
}