package ru.yandex.practicum.filmorate.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.filmorate.utils.AppProperties;

@TestConfiguration
public class TestAppConfig {

    @Bean
    public AppProperties appProperties() {
        return new AppProperties();
    }
}
