package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {
    @MockBean
    private FilmService filmService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppProperties appProperties;

    private FilmDto getValidFilmDto() {
        return FilmDto.builder()
                .name("name")
                .description("description")
                .duration(120L)
                .releaseDate(LocalDate.of(1990, 1, 1)
                        .format(appProperties.getDefaultDateFormatter()))
                .build();
    }

    private UpdateFilmDto getValidUpdateFilmDto() {
        return UpdateFilmDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .duration(120L)
                .releaseDate(LocalDate.of(1990, 1, 1)
                        .format(appProperties.getDefaultDateFormatter()))
                .build();
    }

    private Film getValidFilm() {
        return Film.builder()
                .id(1L)
                .name("name")
                .description("description")
                .duration(Duration.ofMinutes(120))
                .releaseDate(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    public void getFilms_isAvailable() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @Test
    public void createFilm_isAvailable() throws Exception {
        when(filmService.createFilm(any(Film.class))).thenReturn(getValidFilm());
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidFilmDto())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateCreatedFilm_isAvailable() throws Exception {
        when(filmService.updateFilm(any(Film.class))).thenReturn(Optional.of(getValidFilm()));
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidUpdateFilmDto())))
                .andExpect(status().isOk());
    }

    @Test
    public void getFilmById_isAvailable() throws Exception {
        when(filmService.getFilmById(anyLong())).thenReturn(Optional.of(getValidFilm()));
        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void likeFilm_isAvailable() throws Exception {
        when(filmService.likeFilm(anyLong(), anyLong())).thenReturn(getValidFilm());
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void unlikeFilm_isAvailable() throws Exception {
        when(filmService.unlikeFilm(anyLong(), anyLong())).thenReturn(getValidFilm());
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getMostPopularFilms_isAvailable() throws Exception {
        when(filmService.getMostPopularFilms(anyInt())).thenReturn(List.of(getValidFilm()));
        mockMvc.perform(get("/films/popular?count=10"))
                .andExpect(status().isOk());
    }
}