package ru.yandex.practicum.filmorate.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.filmorate.converters.DirectorGenericConverter;
import ru.yandex.practicum.filmorate.converters.FilmGenericConverter;
import ru.yandex.practicum.filmorate.converters.GenreGenericConverter;
import ru.yandex.practicum.filmorate.converters.UserGenericConverter;
import ru.yandex.practicum.filmorate.utils.AppProperties;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AppProperties appProperties;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new FilmGenericConverter(appProperties));
        registry.addConverter(new UserGenericConverter(appProperties));
        registry.addConverter(new GenreGenericConverter());
        registry.addConverter(new DirectorGenericConverter());
    }
}
