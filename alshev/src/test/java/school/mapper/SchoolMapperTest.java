package school.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.dto.SchoolCreateDTO;
import school.dto.SchoolEntityDTO;
import school.entity.SchoolEntity;
import school.mapper.SchoolMapper;


import static org.junit.jupiter.api.Assertions.*;

class SchoolMapperTest {

    private final SchoolMapper mapper = Mappers.getMapper(SchoolMapper.class);

    @Test
    void toEntity_FromCreateDTO_ShouldMapAllFields() {
        // Given
        SchoolCreateDTO createDTO = new SchoolCreateDTO();
        createDTO.setName("Test School");
        createDTO.setAddress("Test Address");

        // When
        SchoolEntity entity = mapper.toEntity(createDTO);

        // Then
        assertNotNull(entity);
        assertEquals(createDTO.getName(), entity.getName());
        assertEquals(createDTO.getAddress(), entity.getAddress());
    }

    @Test
    void toDto_FromEntity_ShouldMapAllFields() {
        // Given
        SchoolEntity entity = new SchoolEntity();
        entity.setId(1L);
        entity.setName("Test School");
        entity.setAddress("Test Address");

        // When
        SchoolEntityDTO dto = mapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getAddress(), dto.getAddress());
    }

    @Test
    void toEntity_FromEntityDTO_ShouldMapAllFields() {
        // Given
        SchoolEntityDTO dto = new SchoolEntityDTO();
        dto.setId(1L);
        dto.setName("Test School");
        dto.setAddress("Test Address");

        // When
        SchoolEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getAddress(), entity.getAddress());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateOnlyNonNullFields() {
        // Given
        SchoolEntity existingEntity = new SchoolEntity();
        existingEntity.setId(1L);
        existingEntity.setName("Original Name");
        existingEntity.setAddress("Original Address");

        SchoolEntityDTO updateDto = new SchoolEntityDTO();
        updateDto.setName("New Name");
        updateDto.setAddress(null); // Этот параметр не должен обновиться

        // When
        mapper.updateEntityFromDto(updateDto, existingEntity);

        // Then
        assertEquals("New Name", existingEntity.getName());
        assertEquals("Original Address", existingEntity.getAddress());
        assertEquals(1L, existingEntity.getId());
    }

    @Test
    void toEntity_FromCreateDTO_WithNullValues_ShouldNotThrowException() {
        // Given
        SchoolCreateDTO createDTO = new SchoolCreateDTO();

        // When
        SchoolEntity entity = mapper.toEntity(createDTO);

        // Then
        assertNotNull(entity);
        assertNull(entity.getName());
        assertNull(entity.getAddress());
    }

    @Test
    void toDto_FromEntity_WithNullValues_ShouldNotThrowException() {
        // Given
        SchoolEntity entity = new SchoolEntity();

        // When
        SchoolEntityDTO dto = mapper.toDto(entity);

        // Then
        assertNotNull(dto);
        assertNull(dto.getName());
        assertNull(dto.getAddress());
    }

    @Test
    void updateEntityFromDto_WithAllNullValues_ShouldNotChangeOriginalValues() {
        // Given
        SchoolEntity existingEntity = new SchoolEntity();
        existingEntity.setId(1L);
        existingEntity.setName("Original Name");
        existingEntity.setAddress("Original Address");

        SchoolEntityDTO updateDto = new SchoolEntityDTO();

        // When
        mapper.updateEntityFromDto(updateDto, existingEntity);

        // Then
        assertEquals("Original Name", existingEntity.getName());
        assertEquals("Original Address", existingEntity.getAddress());
        assertEquals(1L, existingEntity.getId());
    }
}