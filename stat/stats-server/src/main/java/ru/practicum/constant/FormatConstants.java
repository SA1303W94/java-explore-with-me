package ru.practicum.constant;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class FormatConstants {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}