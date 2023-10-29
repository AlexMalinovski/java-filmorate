package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.LongIdDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class FilmGenericConverterTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ConversionService conversionService;

    private String getValidReleaseDate() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        return date.format(appProperties.getDefaultDateFormatter());
    }

    @Test
    public void updateFilmDto_to_Film_isConvertible() {
        UpdateFilmDto dto = UpdateFilmDto.builder()
                .id(1L)
                .name("name")
                .description("descr")
                .releaseDate(getValidReleaseDate())
                .duration(120)
                .mpa(new LongIdDto(1))
                .build();
        Film film = conversionService.convert(dto, Film.class);
        assertNotNull(film);
        assertEquals(dto.getId(), (long) film.getId());
        assertEquals(dto.getName(), film.getName());
        assertEquals(dto.getDescription(), film.getDescription());
        assertEquals(dto.getReleaseDate(), film.getReleaseDate().format(appProperties.getDefaultDateFormatter()));
        assertEquals(dto.getDuration(), film.getDuration().toMinutes());
    }

    @Test
    public void film_to_createdFilmDto_isConvertible() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description("descr")
                .releaseDate(date)
                .duration(Duration.ofMinutes(120))
                .rating(FilmRating.G)
                .build();
        CreatedFilmDto dto = conversionService.convert(film, CreatedFilmDto.class);
        assertNotNull(dto);
        assertEquals(film.getId(), dto.getId());
        assertEquals(film.getName(), dto.getName());
        assertEquals(film.getDescription(), dto.getDescription());
        assertEquals(film.getReleaseDate().format(appProperties.getDefaultDateFormatter()), dto.getReleaseDate());
        assertEquals(film.getDuration().toMinutes(), dto.getDuration());
    }

    @Test
    public void filmDto_to_Film_isConvertible() {
        FilmDto dto = new FilmDto("name", "descr", getValidReleaseDate(),
                120, new LongIdDto(1), new ArrayList<>(), new ArrayList<>());
        Film film = conversionService.convert(dto, Film.class);
        assertNotNull(film);
        assertNull(film.getId());
        assertEquals(dto.getName(), film.getName());
        assertEquals(dto.getDescription(), film.getDescription());
        assertEquals(dto.getReleaseDate(), film.getReleaseDate().format(appProperties.getDefaultDateFormatter()));
        assertEquals(dto.getDuration(), film.getDuration().toMinutes());
    }


}