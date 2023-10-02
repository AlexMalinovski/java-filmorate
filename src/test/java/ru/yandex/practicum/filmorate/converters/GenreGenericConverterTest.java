package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.dto.CreatedGenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GenreGenericConverterTest {

    @Autowired
    private ConversionService conversionService;

    @Test
    public void genre_to_createdGenreDto_isConvertible() {
        Genre genre = Genre.builder()
                .id(1L)
                .name("name")
                .build();
        CreatedGenreDto dto = conversionService.convert(genre, CreatedGenreDto.class);
        assertNotNull(dto);
        assertEquals(genre.getId(), dto.getId());
        assertEquals(genre.getName(), dto.getName());
    }

}