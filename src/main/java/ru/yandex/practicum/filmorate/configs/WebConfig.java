package ru.yandex.practicum.filmorate.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.filmorate.converters.*;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AppProperties appProperties;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FilmGenericConverter(appProperties));
        registry.addConverter(new UserGenericConverter(appProperties));
    }
}
