package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.configs.TestAppConfig;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestAppConfig.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserDto getValidUserDto() {
        return UserDto.builder()
                .name("name")
                .login("login")
                .email("e@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1)
                        .format(appProperties.getDefaultDateFormatter()))
                .build();
    }

    private UpdateUserDto getValidUpdateUserDto() {
        return UpdateUserDto.builder()
                .id(1L)
                .name("name")
                .login("login")
                .email("e@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1)
                        .format(appProperties.getDefaultDateFormatter()))
                .build();
    }

    private User getValidUser() {
        return User.builder()
                .id(1L)
                .name("name")
                .login("login")
                .email("e@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    public void getUsers_isAvailable() throws Exception {
        when(userService.getUsers()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void createUser_isAvailable() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(getValidUser());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidUserDto())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_isAvailable() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(Optional.of(getValidUser()));
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidUpdateUserDto())))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserById_isAvailable() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(getValidUser()));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void addAsFriend_isAvailable() throws Exception {
        when(userService.addAsFriend(anyLong(), anyLong())).thenReturn(getValidUser());
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void removeFromFriends_isAvailable() throws Exception {
        when(userService.removeFromFriends(anyLong(), anyLong())).thenReturn(getValidUser());
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserFriends_isAvailable() throws Exception {
        when(userService.getUserFriends(anyLong())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCommonFriends_isAvailable() throws Exception {
        when(userService.getCommonFriends(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk());
    }

}