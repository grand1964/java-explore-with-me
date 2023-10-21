package ru.practicum.ewm.common.convert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeConverter {
    public static final String timePattern = "yyyy-MM-dd HH:mm:ss";

    /////////////////////////// Форматирование даты //////////////////////////

    public static String formatTime(Timestamp timestamp) {
        return new SimpleDateFormat(timePattern).format(timestamp);
    }

    public static String formatNow() {
        return formatTime(Timestamp.from(Instant.now()));
    }

    //время начала текущего дня (используется в статистике)
    public static String formatCurrentDay() {
        String now = formatNow();
        int spaceIndex = now.indexOf(' ');
        return now.substring(0, spaceIndex) + " 00:00:00";
    }

    public static LocalDateTime dateFromString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timePattern);
        return LocalDateTime.parse(dateString, formatter);
    }

    public static Timestamp timestampFromString(String dateString) {
        return Timestamp.valueOf(dateFromString(dateString));
    }

    /////////////////////////// Оперирование датами //////////////////////////

    public static long getDistanceInMinutes(String date1, String date2) {
        long t1 = timestampFromString(date1).getTime();
        long t2 = timestampFromString(date2).getTime();
        return (t1 - t2) / 60000L;
    }

    //сравнение дат как строк
    public static boolean afterOrEquals(String date1, String date2) {
        return date1.compareTo(date2) >= 0;
    }

    /////////////////////////// Поддержка валидации //////////////////////////

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
