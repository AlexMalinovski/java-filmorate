package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConverterIntegrationTest {
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private ConversionService conversionService;

    private String getValidReleaseDate() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        return date.format(appProperties.getDefaultDateFormatter());
    }

    private String getValidBirthdayDate() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        return date.format(appProperties.getDefaultDateFormatter());
    }

    @Test
    public void createdFilmDto_to_Film_isConvertible() {
        CreatedFilmDto dto = new CreatedFilmDto(1L, "name", "descr", getValidReleaseDate(), 120);
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
        Film film = new Film(1L, "name", "descr", date, Duration.ofMinutes(120));
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
        FilmDto dto = new FilmDto("name", "descr", getValidReleaseDate(), 120);
        Film film = conversionService.convert(dto, Film.class);
        assertNotNull(film);
        assertNull(film.getId());
        assertEquals(dto.getName(), film.getName());
        assertEquals(dto.getDescription(), film.getDescription());
        assertEquals(dto.getReleaseDate(), film.getReleaseDate().format(appProperties.getDefaultDateFormatter()));
        assertEquals(dto.getDuration(), film.getDuration().toMinutes());
    }

    @Test
    public void createdUserDto_to_User_isConvertible() {
        CreatedUserDto dto = new CreatedUserDto(1L, "e@m.ru", "login", "name", getValidBirthdayDate());
        User user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getId(), user.getId());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getLogin(), user.getLogin());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getBirthday(), user.getBirthday().format(appProperties.getDefaultDateFormatter()));
    }

    @Test
    public void user_to_CreatedUserDto_isConvertible() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        User user = new User(1L, "e@m.ru", "login", "name", date);
        CreatedUserDto dto = conversionService.convert(user, CreatedUserDto.class);
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getLogin(), dto.getLogin());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getBirthday().format(appProperties.getDefaultDateFormatter()), dto.getBirthday());
    }

    @Test
    public void userDto_to_User_isConvertible() {
        UserDto dto = new UserDto("e@m.ru", "login", "name", getValidBirthdayDate());
        User user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getLogin(), user.getLogin());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getBirthday(), user.getBirthday().format(appProperties.getDefaultDateFormatter()));
    }

    @Test
    public void ifUserNameNullOrEmptyInUserDto_setNameAsLoginInUser() {
        UserDto dto;
        User user;
        dto = new UserDto("e@m.ru", "login", null, getValidBirthdayDate());
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());

        dto = new UserDto("e@m.ru", "login", "", getValidBirthdayDate());
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());

        dto = new UserDto("e@m.ru", "login", " ", getValidBirthdayDate());
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());
    }

    @Test
    public void ifUserNameNullOrEmptyInCreatedUserDto_setNameAsLoginInUser() {
        CreatedUserDto dto;
        User user;
        dto = new CreatedUserDto(1L,"e@m.ru", "login", null, getValidBirthdayDate());
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());

        dto = new CreatedUserDto(1L, "e@m.ru", "login", "", getValidBirthdayDate());
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());

        dto = new CreatedUserDto(1L, "e@m.ru", "login", " ", getValidBirthdayDate());
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());
    }
}