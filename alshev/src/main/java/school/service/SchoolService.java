package school.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.SchoolEntity;
import school.entity.SubscriberEntity;
import school.exception.ResourceNotFoundException;
import school.mapper.SchoolMapper;
import school.mapper.SubscriberMapper;
import school.repository.SchoolRepository;
import school.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;
    private final ThreadService threadService;
    private final SchoolNotificationSender schoolNotificationSender;
    private final SchoolNotificationThread notificationThread;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;

    @Autowired
    public SchoolService (SchoolRepository schoolRepository, SchoolMapper schoolMapper,  ThreadService threadService, SchoolNotificationSender schoolNotificationSender, SchoolNotificationThread notificationThread, SubscriberRepository subscriberRepository, SubscriberMapper subscriberMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.threadService = threadService;
        this.schoolNotificationSender = schoolNotificationSender;
        this.notificationThread = notificationThread;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
    }

@Transactional
public SchoolEntityDTO create(SchoolCreateDTO schoolCreateDTO) {
    SchoolEntity newSchool = schoolMapper.toEntity(schoolCreateDTO);
    SchoolEntity savedSchool = schoolRepository.save(newSchool);

    SchoolEntityDTO createdDto = schoolMapper.toDto(savedSchool);

    List<SubscriberDto> subscribers = getSubscribersForSchool(savedSchool);
    for (SubscriberDto subscriber : subscribers) {
        if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_CREATE)) {
            schoolNotificationSender.sendCreate(createdDto, subscriber);
        }
    }

    threadService.addSchoolCreated(createdDto);
    return createdDto;
}
    public SchoolEntityDTO findById(Long id) {
        return schoolRepository.findById(id)
                .map(schoolMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
    }

    public Page<SchoolEntityDTO> getAllSchoolsPaged(Pageable pageable) {
        Page<SchoolEntity> schoolPage = schoolRepository.findAll(pageable);
        return schoolPage.map(schoolMapper::toDto);
    }
    public List<SchoolEntityDTO> getAllSchools() {
        List<SchoolEntity> schools = schoolRepository.findAll();
        return schools.stream().map(schoolMapper::toDto).collect(Collectors.toList());
    }

    public SchoolEntityDTO update(Long id, SchoolEntityDTO schoolEntityDTO) {
        Optional<SchoolEntity> fromDb = schoolRepository.findById(id);
        if (!fromDb.isPresent()) {
            throw new ResourceNotFoundException("School not found with id: " + id);
        }
        SchoolEntity oldSchool = fromDb.get();

        schoolMapper.updateEntityFromDto(schoolEntityDTO, oldSchool);
        SchoolEntity updatedSchool = schoolRepository.save(oldSchool);

        SchoolUpdateDto schoolUpdateDto = new SchoolUpdateDto(schoolMapper.toDto(oldSchool), schoolEntityDTO);

        List<SubscriberDto> subscribers = getSubscribersForSchool(updatedSchool);

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_UPDATE)) {
                schoolNotificationSender.sendUpdate(schoolUpdateDto, subscriber);
            }
        }

        threadService.addSchoolUpdated(schoolUpdateDto);

        return schoolMapper.toDto(updatedSchool);
    }
public List<SubscriberDto> getSubscribersForSchool(SchoolEntity schoolEntity) {
    List<SubscriberEntity> subscribers = subscriberRepository.findByEntity(
            SubscriberDto.ENTITY_SCHOOL
    );

    log.info("DEBUG: Searching for subscribers with entity: {}", SubscriberDto.ENTITY_SCHOOL);
    log.info("DEBUG: Total subscribers found: {}", subscribers.size());

    if (subscribers.isEmpty()) {
        log.warn("No subscribers found for school entity");
        return Collections.emptyList();
    }

    for (SubscriberEntity subscriber : subscribers) {
        log.info("DEBUG Subscriber: ID={}, Entity={}, EventType={}, URL={}",
                subscriber.getId(),
                subscriber.getEntity(),
                subscriber.getEventType(),
                subscriber.getUrl()
        );
    }

    return subscribers.stream()
            .map(subscriberMapper::toDto)
            .collect(Collectors.toList());
}
    public void delete(Long id) {
        SchoolEntity existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));

        SchoolEntityDTO schoolDto = schoolMapper.toDto(existingSchool);

        List<SubscriberDto> subscribers = getSubscribersForSchool(existingSchool);
        List<SubscriberDto> deleteSubscribers = subscribers.stream()
                .filter(s -> s.getEventType().equals(SubscriberDto.EVENT_ON_DELETE))
                .collect(Collectors.toList());

        if (!deleteSubscribers.isEmpty()) {
            for (SubscriberDto subscriber : deleteSubscribers) {
                schoolNotificationSender.sendDelete(schoolDto, subscriber);
            }
        }

        schoolRepository.delete(existingSchool);
        threadService.addSchoolDeleted(schoolDto);
    }
}