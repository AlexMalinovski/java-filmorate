package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controllers-DTO для передачи сведений об ошибке
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ProblemDetail {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSxxx");
    private final String timestamp;
    private final int status;
    private final String error;
    private final String path;
    @Nullable
    private String message;
    @Nullable
    private Map<String, Object> properties;

    public ProblemDetail(HttpStatus httpStatus, String path, String message) {
        this(httpStatus, path);
        this.message = message;
    }

    public ProblemDetail(HttpStatus httpStatus, String path) {
        ZoneId zoneUtc = ZoneId.of("UTC");
        this.timestamp = ZonedDateTime.now(zoneUtc).format(ISO_FORMATTER);
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.path = path;
    }

    public void setProperty(String name, @Nullable Object value) {
        this.properties = this.properties != null ? this.properties : new LinkedHashMap<>();
        this.properties.put(name, value);
    }
}
