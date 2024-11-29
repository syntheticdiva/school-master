package school.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SchoolEntityDTOTest {

    private Validator validator;
    private SchoolEntityDTO schoolDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        schoolDTO = new SchoolEntityDTO();
    }

    @Test
    void whenAllFieldsValid_thenNoValidationErrors() {
        // Given
        schoolDTO.setId(1L);
        schoolDTO.setName("Valid School Name");
        schoolDTO.setAddress("Valid Address");

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsNull_thenValidationFails() {
        // Given
        schoolDTO.setAddress("Valid Address");

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name is required",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        // Given
        schoolDTO.setName("  ");
        schoolDTO.setAddress("Valid Address");

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name is required",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenNameIsTooLong_thenValidationFails() {
        // Given
        String longName = "a".repeat(101);
        schoolDTO.setName(longName);
        schoolDTO.setAddress("Valid Address");

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name must be between 2 and 100 characters",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenAddressIsNull_thenValidationFails() {
        // Given
        schoolDTO.setName("Valid School Name");

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Address is required",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenAddressIsBlank_thenValidationFails() {
        // Given
        schoolDTO.setName("Valid School Name");
        schoolDTO.setAddress("   ");

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Address is required",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenAddressIsTooLong_thenValidationFails() {
        // Given
        schoolDTO.setName("Valid School Name");
        schoolDTO.setAddress("a".repeat(201));

        // When
        var violations = validator.validate(schoolDTO);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Address must not exceed 200 characters",
                violations.iterator().next().getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        SchoolEntityDTO dto1 = new SchoolEntityDTO();
        dto1.setId(1L);
        dto1.setName("School");
        dto1.setAddress("Address");

        SchoolEntityDTO dto2 = new SchoolEntityDTO();
        dto2.setId(1L);
        dto2.setName("School");
        dto2.setAddress("Address");

        // Then
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        schoolDTO.setId(1L);
        schoolDTO.setName("Test School");
        schoolDTO.setAddress("Test Address");

        // When
        String toString = schoolDTO.toString();

        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test School"));
        assertTrue(toString.contains("address=Test Address"));
    }
}