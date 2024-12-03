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
import school.entity.SchoolEvent;
import school.exception.ResourceNotFoundException;
import school.mapper.SchoolMapper;
import school.repository.SchoolRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;
    private final NotificationService notificationService;
    private final ThreadService threadService;
    private final SchoolNotificationSender schoolNotificationSender;
    private final SchoolNotificationThread notificationThread;

    @Autowired
    public SchoolService (SchoolRepository schoolRepository, SchoolMapper schoolMapper, NotificationService notificationService, ThreadService threadService, SchoolNotificationSender schoolNotificationSender, SchoolNotificationThread notificationThread) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.notificationService = notificationService;
        this.threadService = threadService;
        this.schoolNotificationSender = schoolNotificationSender;
        this.notificationThread = notificationThread;
    }

    @Transactional
    public SchoolEntityDTO create(SchoolCreateDTO schoolCreateDTO) {
        SchoolEntity newSchool = schoolMapper.toEntity(schoolCreateDTO);
        SchoolEntity savedSchool = schoolRepository.save(newSchool);

        log.info("Saved school with ID: " + savedSchool.getId());

        // Логируем созданный DTO
        SchoolEntityDTO createdDto = schoolMapper.toDto(savedSchool);
        log.info("Created DTO: " + createdDto); // Проверяем, что ID установлен

        // Передаем DTO с установленным ID в поток уведомлений
        threadService.addSchoolCreated(createdDto);

        return createdDto; // Возвращаем созданный DTO
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
        SchoolEntityDTO old = schoolMapper.toDto(fromDb.get());
        schoolMapper.updateEntityFromDto(schoolEntityDTO, fromDb.get());
        SchoolEntity updatedSchool = schoolRepository.save(fromDb.get());
        SchoolUpdateDto schoolUpdateDto = new SchoolUpdateDto(old, schoolEntityDTO);
        List<SubscriberDto> subscribers = getSubscribersForSchool(updatedSchool);
        for (SubscriberDto subscriber : subscribers) {
            schoolNotificationSender.sendUpdate(schoolUpdateDto, subscriber);
        }
        threadService.addSchoolUpdated(schoolUpdateDto);

        return schoolMapper.toDto(updatedSchool);
    }

    private List<SubscriberDto> getSubscribersForSchool(SchoolEntity updatedSchool) {
        List<SubscriberDto> subscribers = new ArrayList<>();
        return subscribers;
    }

public void delete(Long id) {
    Optional<SchoolEntity> optionalSchool = schoolRepository.findById(id);
    if (!optionalSchool.isPresent()) {
        throw new ResourceNotFoundException("School not found with id: " + id);
    }
    SchoolEntity existingSchool = optionalSchool.get();
    schoolRepository.delete(existingSchool);
    threadService.addSchoolDeleted(schoolMapper.toDto(existingSchool));
}
}