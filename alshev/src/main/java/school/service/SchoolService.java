package school.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.dto.SchoolUpdateDto;
import school.dto.SubscriberDto;
import school.entity.SchoolEntity;
import school.entity.SubscriberEntity;
import school.exception.NotificationProcessingException;
import school.exception.NotificationSendingException;
import school.exception.ResourceNotFoundException;
import school.exception.SchoolServiceException;
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
    private static final int EXPECTED_SUBSCRIBER_COUNT = 1;
    @Autowired
    public SchoolService(SchoolRepository schoolRepository,
                         SchoolMapper schoolMapper,
                         SchoolNotificationSender schoolNotificationSender,
                         SubscriberRepository subscriberRepository,
                         SubscriberMapper subscriberMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.schoolNotificationSender = schoolNotificationSender;
        this.subscriberRepository = subscriberRepository;
        this.subscriberMapper = subscriberMapper;
    }
    @Transactional
    public SchoolEntityDTO create(SchoolCreateDTO schoolCreateDTO) {
        try {
            SchoolEntity newSchool = schoolMapper.toEntity(schoolCreateDTO);
            SchoolEntity savedSchool = schoolRepository.save(newSchool);
            SchoolEntityDTO createdDto = schoolMapper.toDto(savedSchool);

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
        } catch (DataAccessException e) {
            throw new SchoolServiceException("Error creating the school due to data access issue", e);
        } catch (RuntimeException e) {
            throw new SchoolServiceException("Error creating the school", e);
        }
    }
    public SchoolEntityDTO update(Long id, SchoolEntityDTO schoolEntityDTO) {
        Optional<SchoolEntity> fromDb = schoolRepository.findById(id);
        if (!fromDb.isPresent()) {
            throw new ResourceNotFoundException("School not found with id: " + id);
        }
        SchoolEntityDTO old = schoolMapper.toDto(fromDb.get());
        schoolMapper.updateEntityFromDto(schoolEntityDTO, fromDb.get());
        SchoolEntity updatedSchool = schoolRepository.save(fromDb.get());
        SchoolUpdateDto schoolUpdateDto = new SchoolUpdateDto(old, schoolEntityDTO);

        List<SubscriberDto> subscribers = getSubscribersForSchool(updatedSchool);
        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_UPDATE)) {
                schoolNotificationSender.sendUpdate(schoolUpdateDto, subscriber);
            }
        }
        return schoolMapper.toDto(updatedSchool);
    }

    public void delete(Long id) {
    try {
        SchoolEntity existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));

        List<SubscriberDto> subscribers = getSubscribersForSchool(existingSchool);

        for (SubscriberDto subscriber : subscribers) {
            if (subscriber.getEventType().equals(SubscriberDto.EVENT_ON_DELETE)) {
                try {
                    SchoolEntityDTO schoolDto = schoolMapper.toDto(existingSchool);
                    schoolNotificationSender.sendDelete(schoolDto, subscriber);
                } catch (NotificationProcessingException e) {
                    log.error("Failed to send delete notification to subscriber ID " + subscriber.getId(), e);
                }
            }
        }

        schoolRepository.delete(existingSchool);
    } catch (DataAccessException e) {
        throw new SchoolServiceException("Error deleting the school due to data access issue", e);
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
        return schools.stream().map(schoolMapper::toDto).collect(Collectors.toList());
    }
    public SchoolEntityDTO findById(Long id) {
        return schoolRepository.findById(id)
                .map(schoolMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));
    }
}