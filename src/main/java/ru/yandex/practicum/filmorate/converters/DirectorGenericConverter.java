package ru.yandex.practicum.filmorate.converters;

import org.springframework.core.convert.converter.GenericConverter;
import ru.yandex.practicum.filmorate.dto.CreatedDirectorDto;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.models.Director;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public class DirectorGenericConverter extends AbstractGenericConverter {

    @Override
    protected Map<ConvertiblePair, Function<Object, Object>> setSupportConversions() {
        return Map.ofEntries(
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(UpdateDirectorDto.class, Director.class),
                        this::convertUpdateDirectorDtoToDirector),
                new AbstractMap.SimpleEntry<>(
                        new GenericConverter.ConvertiblePair(DirectorDto.class, Director.class),
                        this::convertDirectorDtoToDirector),
                new AbstractMap.SimpleEntry<>(
                        new GenericConverter.ConvertiblePair(Director.class, CreatedDirectorDto.class),
                        this::convertDirectorToCreatedDirectorDto));
    }

    private Director convertUpdateDirectorDtoToDirector(Object updateDirectorDto) {
        UpdateDirectorDto dto = (UpdateDirectorDto) updateDirectorDto;
        return Director.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    private Director convertDirectorDtoToDirector(Object directorDto) {
        DirectorDto dto = (DirectorDto) directorDto;
        return Director.builder()
                .name(dto.getName())
                .build();
    }

    private CreatedDirectorDto convertDirectorToCreatedDirectorDto(Object directorObj) {
        Director director = (Director) directorObj;
        return CreatedDirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }
}
