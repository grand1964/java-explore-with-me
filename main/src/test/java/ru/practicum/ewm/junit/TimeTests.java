package ru.practicum.ewm.junit;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.common.convert.TimeConverter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeTests {
    @Test
    public void timeValidateTest() {
        assertTrue(TimeConverter.validateDateTime("2007-09-06 00:11:22"));
        assertFalse(TimeConverter.validateDateTime("2007-09-06%2013%3A30%3A38"));
    }
}
