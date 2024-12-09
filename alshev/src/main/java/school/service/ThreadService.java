package school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.SubscriberEntity;
import school.mapper.SubscriberMapper;
import school.repository.NotificationStatusRepository;
import school.repository.SubscriberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreadService {
    private SchoolNotificationThread schoolNotificationThread = null;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;
    private final SchoolNotificationSender notificationSender;
    private final NotificationStatusRepository notificationStatusRepository;

    @Autowired
    public ThreadService(
            SubscriberRepository subscriberRepository,
            SubscriberMapper subscriberMapper,
            SchoolNotificationSender notificationSender,
            NotificationStatusRepository notificationStatusRepository
    ) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
        this.notificationSender = notificationSender;
        this.notificationStatusRepository = notificationStatusRepository;
    }

    private void checkAndStart() {
        if (schoolNotificationThread != null)
            return;
        schoolNotificationThread = new SchoolNotificationThread(notificationSender, notificationStatusRepository);
        schoolNotificationThread.start();
    }

    public void addSubscriber(SubscriberDto subscriberDto) {
        checkAndStart();
        schoolNotificationThread.addSubscriber(subscriberDto);
        log.info("Added subscriber ID={}, URL={}",
                subscriberDto.getId(), subscriberDto.getUrl());
    }

    public void removeSubscriber(Long subscriberId) {
        checkAndStart();
        schoolNotificationThread.removeSubscriber(subscriberId);
    }

    public List<SubscriberDto> getSubscribers() {
        List<SubscriberEntity> subscriberEntities = subscriberRepository.findByEntity(SubscriberDto.ENTITY_SCHOOL);

        return subscriberEntities.stream()
                .map(subscriberMapper::toDto)
                .collect(Collectors.toList());
    }

    public void addSchoolCreated(SchoolEntityDTO schoolEntityDTO) {
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