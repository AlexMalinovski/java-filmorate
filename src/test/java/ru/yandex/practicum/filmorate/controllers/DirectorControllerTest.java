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
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DirectorController.class)
@Import(TestAppConfig.class)
class DirectorControllerTest {

    @MockBean
    private DirectorService directorService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppProperties appProperties;

    private DirectorDto getValidDirectorDto() {
        return DirectorDto.builder()
                .id(1L)
                .name("name")
                .build();
    }

    private UpdateDirectorDto getValidUpdateDirectorDto() {
        return UpdateDirectorDto.builder()
                .id(1L)
                .name("name")
                .build();
    }

    private Director getValidDirector() {
        return Director.builder()
                .id(1L)
                .name("name")
                .build();
    }

    @Test
    public void getDirectors_isAvailable() throws Exception {
        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk());
    }

    @Test
    public void createDirector_isAvailable() throws Exception {
        when(directorService.createDirector(any(Director.class))).thenReturn(getValidDirector());
        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidDirectorDto())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateCreatedDirector_isAvailable() throws Exception {
        when(directorService.updateDirector(any(Director.class))).thenReturn(Optional.of(getValidDirector()));
        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidUpdateDirectorDto())))
                .andExpect(status().isOk());
    }

    @Test
    public void getDirectorById_isAvailable() throws Exception {
        when(directorService.getDirectorById(anyLong())).thenReturn(getValidDirector());
        mockMvc.perform(get("/directors/1"))
                .andExpect(status().isOk());
    }
}