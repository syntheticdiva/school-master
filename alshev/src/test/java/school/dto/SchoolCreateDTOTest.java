package school.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SchoolCreateDTOTest {

    @Test
    void testConstructorAndGetters() {
        SchoolCreateDTO dto = new SchoolCreateDTO();
        assertNull(dto.getName());
        assertNull(dto.getAddress());
    }

    @Test
    void testSetters() {
        SchoolCreateDTO dto = new SchoolCreateDTO();

        dto.setName("Test School");
        assertEquals("Test School", dto.getName());

        dto.setAddress("123 Test Street");
        assertEquals("123 Test Street", dto.getAddress());
    }

    @Test
    void testEqualsAndHashCode() {
        SchoolCreateDTO dto1 = new SchoolCreateDTO();
        dto1.setName("Test School");
        dto1.setAddress("123 Test Street");

        SchoolCreateDTO dto2 = new SchoolCreateDTO();
        dto2.setName("Test School");
        dto2.setAddress("123 Test Street");

        SchoolCreateDTO dto3 = new SchoolCreateDTO();
        dto3.setName("Another School");
        dto3.setAddress("456 Other Street");

        // Test equals
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);

        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        SchoolCreateDTO dto = new SchoolCreateDTO();
        dto.setName("Test School");
        dto.setAddress("123 Test Street");

        String toString = dto.toString();

        assertTrue(toString.contains("name=Test School"));
        assertTrue(toString.contains("address=123 Test Street"));
    }

    @Test
    void testDifferentAddressSameNameNotEqual() {
        SchoolCreateDTO dto1 = new SchoolCreateDTO();
        dto1.setName("Test School");
        dto1.setAddress("123 Test Street");

        SchoolCreateDTO dto2 = new SchoolCreateDTO();
        dto2.setName("Test School");
        dto2.setAddress("456 Other Street");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testDifferentNameSameAddressNotEqual() {
        SchoolCreateDTO dto1 = new SchoolCreateDTO();
        dto1.setName("Test School");
        dto1.setAddress("123 Test Street");

        SchoolCreateDTO dto2 = new SchoolCreateDTO();
        dto2.setName("Another School");
        dto2.setAddress("123 Test Street");

        assertNotEquals(dto1, dto2);
    }
}