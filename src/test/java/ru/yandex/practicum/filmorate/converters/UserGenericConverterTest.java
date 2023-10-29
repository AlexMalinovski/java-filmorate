package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.configs.TestAppConfig;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Import(TestAppConfig.class)
class UserGenericConverterTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ConversionService conversionService;

    private String getValidBirthdayDate() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        return date.format(appProperties.getDefaultDateFormatter());
    }

    @Test
    public void updateUserDto_to_User_isConvertible() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(1L)
                .email("e@m.ru")
                .login("login")
                .name("name")
                .birthday(getValidBirthdayDate()).build();
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
        User user = User.builder()
                .id(1L)
                .email("e@m.ru")
                .login("login")
                .name("name")
                .birthday(date)
                .build();
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
    public void ifUserNameNullOrEmptyInUpdateUserDto_setNameAsLoginInUser() {
        UpdateUserDto dto;
        User user;
        dto = UpdateUserDto.builder()
                .id(1L)
                .email("e@m.ru")
                .login("login")
                .name(null)
                .birthday(getValidBirthdayDate())
                .build();
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());

        dto = UpdateUserDto.builder()
                .id(1L)
                .email("e@m.ru")
                .login("login")
                .name("")
                .birthday(getValidBirthdayDate())
                .build();
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());

        dto = UpdateUserDto.builder()
                .id(1L)
                .email("e@m.ru")
                .login("login")
                .name(" ")
                .birthday(getValidBirthdayDate())
                .build();
        user = conversionService.convert(dto, User.class);
        assertNotNull(user);
        assertEquals(dto.getLogin(), user.getName());
    }
}