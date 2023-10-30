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
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.LongIdDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.SearchService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@Import(TestAppConfig.class)
class FilmControllerTest {
    @MockBean
    private FilmService filmService;

    @MockBean
    private UserService userService;

    @MockBean
    private SearchService searchService;

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
                .mpa(new LongIdDto(1))
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
                .mpa(new LongIdDto(1))
                .build();
    }

    private Film getValidFilm() {
        return Film.builder()
                .id(1L)
                .name("name")
                .description("description")
                .duration(Duration.ofMinutes(120))
                .releaseDate(LocalDate.of(1990, 1, 1))
                .rating(FilmRating.G)
                .build();
    }

    private Genre getValidGenre() {
        return Genre.builder().id(1L).name("name").build();
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

    @Test
    public void getGenres_isAvailable() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk());
    }

    @Test
    public void getFilmRatings_isAvailable() throws Exception {
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk());
    }

    @Test
    public void getGenreById_isAvailable() throws Exception {
        when(filmService.getGenreById(anyLong())).thenReturn(Optional.of(getValidGenre()));
        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getFilmRatingById_isAvailable() throws Exception {
        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCommonFilms_isAvailable() throws Exception {
        when(filmService.getCommonFilms(anyLong(), anyLong())).thenReturn(List.of(getValidFilm()));
        mockMvc.perform(get("/films/common?userId=1&friendId=2"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteFilmById_isAvailable() throws Exception {
        when(filmService.deleteFilmById((anyLong()))).thenReturn(getValidFilm());
        mockMvc.perform(delete("/films/1"))
                .andExpect(status().isOk());
    }
}