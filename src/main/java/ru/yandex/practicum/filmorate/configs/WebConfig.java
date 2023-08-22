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
        var dateFormatter = appProperties.getDefaultDateFormatter();
        registry.addConverter(new FilmDtoToFilm(dateFormatter));
        registry.addConverter(new FilmToCreatedFilmDto(dateFormatter));
        registry.addConverter(new CreatedFilmDtoToFilm(dateFormatter));
        registry.addConverter(new UserDtoToUser(dateFormatter));
        registry.addConverter(new UserToCreatedUserDto(dateFormatter));
        registry.addConverter(new CreatedUserDtoToUser(dateFormatter));
    }
}
