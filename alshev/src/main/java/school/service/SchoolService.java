package school.service;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.entity.SchoolEntity;
import school.entity.SchoolEvent;
import school.exception.ResourceNotFoundException;
import school.mapper.SchoolMapper;
import school.repository.SchoolRepository;

import java.time.LocalDateTime;


@Service
@Transactional
public class SchoolService {
    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    private final NotificationService notificationService;

    public SchoolService (SchoolRepository schoolRepository, SchoolMapper schoolMapper, NotificationService notificationService) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.notificationService = notificationService;
    }


public SchoolEntityDTO create(SchoolCreateDTO schoolCreateDTO) {
    SchoolEntity school = schoolMapper.toEntity(schoolCreateDTO);
    school = schoolRepository.save(school);
    SchoolEvent event = new SchoolEvent();
    event.setSchoolId(school.getId());
    event.setName(school.getName());
    event.setAddress(school.getAddress());
    event.setDate(LocalDateTime.now());
    event.setEventType("CREATE");

    notificationService.sendNotification(event);

    return schoolMapper.toDto(school);
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

    public SchoolEntityDTO update(Long id, SchoolEntityDTO schoolEntityDTO) {
        SchoolEntity school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));

        String oldName = school.getName();
        String oldAddress = school.getAddress();

        schoolMapper.updateEntityFromDto(schoolEntityDTO, school);
        school = schoolRepository.save(school);

        SchoolEvent event = new SchoolEvent();
        event.setSchoolId(school.getId());
        event.setName(school.getName());
        event.setOldName(oldName);
        event.setOldAddress(oldAddress);
        event.setAddress(school.getAddress());
        event.setDate(LocalDateTime.now());
        event.setEventType("UPDATE");

        notificationService.sendNotification(event);

        return schoolMapper.toDto(school);
    }

    public void deleteById(Long id) {
        SchoolEntity school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + id));

        SchoolEvent event = new SchoolEvent();
        event.setSchoolId(school.getId());
        event.setName(school.getName());
        event.setAddress(school.getAddress());
        event.setDate(LocalDateTime.now());
        event.setEventType("DELETE");

        notificationService.sendNotification(event);

        schoolRepository.deleteById(id);
    }
}