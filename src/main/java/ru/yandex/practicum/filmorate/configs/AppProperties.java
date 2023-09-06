package ru.yandex.practicum.filmorate.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@ConfigurationProperties(prefix = "app.property")
@Getter
public final class AppProperties {
    @Value("${spring.mvc.format.date:yyyy-MM-dd}")
    private String defaultDateFormat;

    public DateTimeFormatter getDefaultDateFormatter() {
        return DateTimeFormatter.ofPattern(defaultDateFormat);
    }
}
