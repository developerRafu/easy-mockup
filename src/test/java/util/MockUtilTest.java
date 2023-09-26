package util;

import model.Person;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MockUtilTest {
    @Test
    public void testCreateMockWithString() {
        Person sample = Mockup.createMock(Person.class);
        assertNotNull(sample);
        assertEquals("string", sample.getName());
    }

    @Test
    public void testCreateMockWithInt() {
        Person sample = Mockup.createMock(Person.class);
        assertNotNull(sample);
        assertEquals(0, sample.getAge());
    }

    @Test
    public void testCreateMockWithBigDecimal() {
        Person sample = Mockup.createMock(Person.class);
        assertNotNull(sample);
        assertEquals(new BigDecimal("0.0"), sample.getSalary());
    }

    @Test
    public void testCreateMockWithLocalDate() {
        Person sample = Mockup.createMock(Person.class);
        assertNotNull(sample);
        assertEquals(LocalDate.now(), sample.getBirthDate());
    }

    @Test
    public void testCreateMockWithCustomValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Test");
        values.put("age", 42);
        Person sample = Mockup.createMock(Person.class, values);
        assertNotNull(sample);
        assertEquals("Test", sample.getName());
        assertEquals(42, sample.getAge());
    }
}