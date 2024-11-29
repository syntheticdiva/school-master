package school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.entity.SchoolEntity;
import school.exception.ResourceNotFoundException;
import school.mapper.SchoolMapper;
import school.repository.SchoolRepository;


import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchoolServiceImplTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private SchoolMapper schoolMapper;

    @InjectMocks
    private SchoolService schoolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldReturnCreatedSchool() {
        // Given
        SchoolCreateDTO createDTO = new SchoolCreateDTO();
        SchoolEntity entity = new SchoolEntity();
        SchoolEntityDTO expectedDTO = new SchoolEntityDTO();

        when(schoolMapper.toEntity(createDTO)).thenReturn(entity);
        when(schoolRepository.save(entity)).thenReturn(entity);
        when(schoolMapper.toDto(entity)).thenReturn(expectedDTO);

        // When
        SchoolEntityDTO result = schoolService.create(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(schoolRepository).save(entity);
    }

    @Test
    void findById_ExistingId_ShouldReturnSchool() {
        // Given
        Long id = 1L;
        SchoolEntity entity = new SchoolEntity();
        SchoolEntityDTO expectedDTO = new SchoolEntityDTO();

        when(schoolRepository.findById(id)).thenReturn(Optional.of(entity));
        when(schoolMapper.toDto(entity)).thenReturn(expectedDTO);

        // When
        SchoolEntityDTO result = schoolService.findById(id);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);
    }

    @Test
    void findById_NonExistingId_ShouldThrowException() {
        // Given
        Long id = 1L;
        when(schoolRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> schoolService.findById(id));
    }

    @Test
    void getAllSchoolsPaged_ShouldReturnPagedResults() {
        // Given
        Pageable pageable = mock(Pageable.class);
        SchoolEntity entity1 = new SchoolEntity();
        SchoolEntity entity2 = new SchoolEntity();
        Page<SchoolEntity> entityPage = new PageImpl<>(Arrays.asList(entity1, entity2));

        when(schoolRepository.findAll(pageable)).thenReturn(entityPage);
        when(schoolMapper.toDto(any(SchoolEntity.class))).thenReturn(new SchoolEntityDTO());

        // When
        Page<SchoolEntityDTO> result = schoolService.getAllSchoolsPaged(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(schoolRepository).findAll(pageable);
        verify(schoolMapper, times(2)).toDto(any(SchoolEntity.class));
    }

    @Test
    void update_ExistingSchool_ShouldReturnUpdatedSchool() {
        // Given
        Long id = 1L;
        SchoolEntityDTO updateDTO = new SchoolEntityDTO();
        SchoolEntity existingEntity = new SchoolEntity();
        SchoolEntity updatedEntity = new SchoolEntity();
        SchoolEntityDTO expectedDTO = new SchoolEntityDTO();

        when(schoolRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(schoolRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(schoolMapper.toDto(updatedEntity)).thenReturn(expectedDTO);

        // When
        SchoolEntityDTO result = schoolService.update(id, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(schoolMapper).updateEntityFromDto(updateDTO, existingEntity);
        verify(schoolRepository).save(existingEntity);
    }

    @Test
    void update_NonExistingSchool_ShouldThrowException() {
        // Given
        Long id = 1L;
        SchoolEntityDTO updateDTO = new SchoolEntityDTO();
        when(schoolRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> schoolService.update(id, updateDTO));
    }

    @Test
    void deleteById_ExistingId_ShouldDeleteSchool() {
        // Given
        Long id = 1L;
        when(schoolRepository.existsById(id)).thenReturn(true);

        // When
        schoolService.deleteById(id);

        // Then
        verify(schoolRepository).deleteById(id);
    }

    @Test
    void deleteById_NonExistingId_ShouldThrowException() {
        // Given
        Long id = 1L;
        when(schoolRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> schoolService.deleteById(id));
    }
}