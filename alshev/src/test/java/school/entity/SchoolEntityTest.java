package school.entity;

import jakarta.persistence.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SchoolEntityTest {

    private Validator validator;
    private SchoolEntity school;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        school = new SchoolEntity();
    }

    @Test
    void testEntityAnnotations() {
        // проверяем наличие необходимых аннотаций на классе
        assertTrue(SchoolEntity.class.isAnnotationPresent(Entity.class));
        assertTrue(SchoolEntity.class.isAnnotationPresent(Table.class));

        Table tableAnnotation = SchoolEntity.class.getAnnotation(Table.class);
        assertEquals("school", tableAnnotation.name());
    }

    @Test
    void testIdField() throws NoSuchFieldException {
        Field idField = SchoolEntity.class.getDeclaredField("id");

        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(GenerationType.IDENTITY, generatedValue.strategy());
    }

    @Test
    void testNameField() throws NoSuchFieldException {
        Field nameField = SchoolEntity.class.getDeclaredField("name");

        assertTrue(nameField.isAnnotationPresent(Column.class));
        Column columnAnnotation = nameField.getAnnotation(Column.class);

        assertEquals("name", columnAnnotation.name());
        assertEquals(100, columnAnnotation.length());
        assertTrue(columnAnnotation.nullable() == false);
    }

    @Test
    void testAddressField() throws NoSuchFieldException {
        Field addressField = SchoolEntity.class.getDeclaredField("address");

        assertTrue(addressField.isAnnotationPresent(Column.class));
        Column columnAnnotation = addressField.getAnnotation(Column.class);

        assertEquals("address", columnAnnotation.name());
        assertEquals(200, columnAnnotation.length());
        assertTrue(columnAnnotation.nullable() == false);
    }

    @Test
    void testAuditFields() throws NoSuchFieldException {
        Field createdAtField = SchoolEntity.class.getDeclaredField("createdAt");
        Field updatedAtField = SchoolEntity.class.getDeclaredField("updatedAt");

        // Проверяем CreatedDate
        assertTrue(createdAtField.isAnnotationPresent(CreatedDate.class));
        Column createdAtColumn = createdAtField.getAnnotation(Column.class);
        assertEquals("created_at", createdAtColumn.name());
        assertFalse(createdAtColumn.updatable());
        assertFalse(createdAtColumn.nullable());

        // Проверяем LastModifiedDate
        assertTrue(updatedAtField.isAnnotationPresent(LastModifiedDate.class));
        Column updatedAtColumn = updatedAtField.getAnnotation(Column.class);
        assertEquals("updated_at", updatedAtColumn.name());
        assertFalse(updatedAtColumn.nullable());
    }

    @Test
    void whenAllFieldsValid_thenNoValidationErrors() {
        school.setName("Valid School Name");
        school.setAddress("Valid Address");
        school.setCreatedAt(LocalDateTime.now());
        school.setUpdatedAt(LocalDateTime.now());

        var violations = validator.validate(school);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsNull_thenValidationFails() {
        school.setAddress("Valid Address");

        var violations = validator.validate(school);
        assertFalse(violations.isEmpty());
        assertEquals("Name is required",
                violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", ""})
    void whenNameIsInvalid_thenValidationFails(String invalidName) {
        school.setName(invalidName);
        school.setAddress("Valid Address");

        var violations = validator.validate(school);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNameIsTooLong_thenValidationFails() {
        school.setName("a".repeat(101));
        school.setAddress("Valid Address");

        var violations = validator.validate(school);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenAddressIsNull_thenValidationFails() {
        school.setName("Valid School Name");

        var violations = validator.validate(school);
        assertFalse(violations.isEmpty());
        assertEquals("Address is required",
                violations.iterator().next().getMessage());
    }

    @Test
    void whenAddressIsTooLong_thenValidationFails() {
        school.setName("Valid School Name");
        school.setAddress("a".repeat(201));

        var violations = validator.validate(school);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        SchoolEntity school1 = new SchoolEntity();
        school1.setId(1L);
        school1.setName("School");
        school1.setAddress("Address");
        LocalDateTime now = LocalDateTime.now();
        school1.setCreatedAt(now);
        school1.setUpdatedAt(now);

        SchoolEntity school2 = new SchoolEntity();
        school2.setId(1L);
        school2.setName("School");
        school2.setAddress("Address");
        school2.setCreatedAt(now);
        school2.setUpdatedAt(now);

        assertEquals(school1, school2);
        assertEquals(school1.hashCode(), school2.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        school.setId(1L);
        school.setName("Test School");
        school.setAddress("Test Address");
        school.setCreatedAt(now);
        school.setUpdatedAt(now);

        String toString = school.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test School"));
        assertTrue(toString.contains("address=Test Address"));
        assertTrue(toString.contains("createdAt=" + now));
        assertTrue(toString.contains("updatedAt=" + now));
    }
}