package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.dto.CreatedDirectorDto;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class DirectorGenericConverterTest {
    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ConversionService conversionService;

    @Test
    public void updateDirectorDto_to_Director_isConvertible() {
        UpdateDirectorDto dto = UpdateDirectorDto.builder()
                .id(1L)
                .name("name")
                .build();
        Director director = conversionService.convert(dto, Director.class);
        assertNotNull(director);
        assertEquals(dto.getId(), (long) director.getId());
        assertEquals(dto.getName(), director.getName());
    }

    @Test
    public void director_to_createdDirectorDto_isConvertible() {
        Director director = Director.builder()
                .id(1L)
                .name("name")
                .build();
        CreatedDirectorDto dto = conversionService.convert(director, CreatedDirectorDto.class);
        assertNotNull(dto);
        assertEquals(director.getId(), dto.getId());
        assertEquals(director.getName(), dto.getName());
    }

    @Test
    public void DirectorDto_to_Director_isConvertible() {
        DirectorDto dto = new DirectorDto(1L, "name");
        Director director = conversionService.convert(dto, Director.class);
        assertNotNull(director);
        assertNull(director.getId());
        assertEquals(dto.getName(), director.getName());
    }
}