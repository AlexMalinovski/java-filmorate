package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getFilms_isAvailable() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk());
    }

    @Test
    public void postFilms_isAvailable() throws Exception {
        mockMvc.perform(post("/films"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putFilms_isAvailable() throws Exception {
        mockMvc.perform(post("/films"))
                .andExpect(status().isBadRequest());
    }

}