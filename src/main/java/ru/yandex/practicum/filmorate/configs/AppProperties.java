package ru.yandex.practicum.filmorate.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@ConfigurationProperties(prefix = "app.prop")
@Data
public class AppProperties {
    private String defaultDateFormat = "yyyy-MM-dd";

    public DateTimeFormatter getDefaultDateFormatter() {
        return DateTimeFormatter.ofPattern(defaultDateFormat);
    }
}
