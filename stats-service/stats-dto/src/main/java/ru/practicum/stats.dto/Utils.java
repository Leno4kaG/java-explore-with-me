package ru.practicum.stats.dto;

import java.time.format.DateTimeFormatter;

public abstract class Utils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final String HIT = "/hit";
    public static final String STATS = "/stats";
}
