package ru.practicum.ewm.convert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeConverter {
    public static final String timePattern = "yyyy-MM-dd HH:mm:ss";

    //валидация диапазона дат
    public static boolean validateRange(String rangeStart, String rangeEnd) {
        if ((rangeStart == null) ^ (rangeEnd == null)) { //задана только одна граница
            return false;
        }
        if (rangeStart == null) { //обе границы не заданы - это нормально
            return true;
        }
        return validateDateTime(rangeStart) && validateDateTime(rangeStart) &&
                (rangeStart.compareTo(rangeEnd) <= 0);
    }

    //валидация формата даты
    public static boolean validateDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timePattern);
        try {
            LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
