package school.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.SchoolEntity;
import school.entity.SubscriberEntity;
import school.exception.*;
import school.mapper.SchoolMapper;
import school.mapper.SubscriberMapper;
import school.repository.SchoolRepository;
import school.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;
    private final SchoolNotificationSender schoolNotificationSender;
    private final SubscriberRepository subscriberRepository;
    private final SubscriberMapper subscriberMapper;
    private final ThreadService threadService;
    private static final int EXPECTED_SUBSCRIBER_COUNT = 1;
    @Autowired
    public SchoolService(SchoolRepository schoolRepository,
                         SchoolMapper schoolMapper,
                         SchoolNotificationSender schoolNotificationSender,
                         SubscriberRepository subscriberRepository,
                         SubscriberMapper subscriberMapper, ThreadService threadService) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.schoolNotificationSender = schoolNotificationSender;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
        this.threadService = threadService;
    }

    @Transactional
    public SchoolEntityDTO create(SchoolCreateDTO schoolCreateDTO) {
        SchoolEntity newSchool = schoolMapper.toEntity(schoolCreateDTO);
        SchoolEntity savedSchool = schoolRepository.save(newSchool);
        SchoolEntityDTO createdDto = schoolMapper.toDto(savedSchool);
        threadService.addSchoolCreated(createdDto);

        List<SubscriberDto> subscribers = getSubscribersForSchool(savedSchool);
        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_CREATE)) {
                try {
                    schoolNotificationSender.sendCreate(createdDto, subscriber);
                } catch (NotificationSendingException e) {
                    log.error("Failed to send creation notification to subscriber ID " + subscriber.getId(), e);
                }
            }
        }

        return createdDto;
    }
    public SchoolEntityDTO update(Long id, SchoolEntityDTO schoolEntityDTO) {
        Optional<SchoolEntity> fromDb = schoolRepository.findById(id);
        if (!fromDb.isPresent()) {
            throw new ResourceNotFoundException(String.valueOf(id));
        }
        SchoolEntityDTO old = schoolMapper.toDto(fromDb.get());
        schoolMapper.updateEntityFromDto(schoolEntityDTO, fromDb.get());
        SchoolEntity updatedSchool = schoolRepository.save(fromDb.get());
        SchoolUpdateDto schoolUpdateDto = new SchoolUpdateDto(old, schoolEntityDTO);
        List<SubscriberDto> subscribers = getSubscribersForSchool(updatedSchool);
        threadService.addSchoolUpdated(schoolUpdateDto);

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_UPDATE)) {
                try {
                    schoolNotificationSender.sendUpdate(schoolUpdateDto, subscriber);
                } catch (NotificationSendingException e) {
                    log.error("Failed to send update notification to subscriber ID {}: {}", subscriber.getId(), e.getMessage());
                } catch (RestClientException e) {
                    log.error("Connection issue while sending update notification to subscriber ID {}: {}", subscriber.getId(), e.getMessage());
                }
            }
        }

        return schoolMapper.toDto(updatedSchool);
    }
    public void delete(Long id) {
        Optional<SchoolEntity> fromDb = schoolRepository.findById(id);
        if (!fromDb.isPresent()) {
            throw new ResourceNotFoundException(String.valueOf(id));
        }

        SchoolEntity existingSchool = fromDb.get();
        SchoolEntityDTO schoolDto = schoolMapper.toDto(existingSchool);
        List<SubscriberDto> subscribers = getSubscribersForSchool(existingSchool);
        threadService.addSchoolDeleted(schoolDto);

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_DELETE)) {
                try {
                    schoolNotificationSender.sendDelete(schoolDto, subscriber);
                } catch (NotificationSendingException e) {
                    log.error("Failed to send delete notification to subscriber ID {}: {}", subscriber.getId(), e.getMessage());
                } catch (RestClientException e) {
                    log.error("Connection issue while sending delete notification to subscriber ID {}: {}", subscriber.getId(), e.getMessage());
                }
            }
        }

        try {
            schoolRepository.delete(existingSchool);
        } catch (DataAccessException e) {
            throw new SchoolServiceException("Error deleting the school", e);
        }
    }

    public List<SubscriberDto> getSubscribersForSchool(SchoolEntity schoolEntity) {
        List<SubscriberEntity> subscribers = subscriberRepository.findByEntity(
                SubscriberDto.ENTITY_SCHOOL
        );
        log.info("DEBUG: Searching for subscribers with entity: {}", SubscriberDto.ENTITY_SCHOOL);

        if (subscribers.isEmpty()) {
            throw new EmptyResultDataAccessException("Subscribers were not found for the specified school.", EXPECTED_SUBSCRIBER_COUNT);
        }
        return subscribers.stream()
                .map(subscriberMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<SchoolEntityDTO> getAllSchoolsPaged(Pageable pageable) {
        Page<SchoolEntity> schoolPage = schoolRepository.findAll(pageable);
        return schoolPage.map(schoolMapper::toDto);
    }

    public List<SchoolEntityDTO> getAllSchools() {
        List<SchoolEntity> schools = schoolRepository.findAll();

        if (schools.isEmpty()) {
            throw new NoContentException("No schools found");
        }

        return schools.stream()
                .map(schoolMapper::toDto)
                .collect(Collectors.toList());
    }
    public SchoolEntityDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidSchoolIdException("The school ID must be a positive number.");
        }

        return schoolRepository.findById(id)
                .map(schoolMapper::toDto)
                .orElseThrow(() -> new SchoolNotFoundException(String.valueOf(id)));
    }

}