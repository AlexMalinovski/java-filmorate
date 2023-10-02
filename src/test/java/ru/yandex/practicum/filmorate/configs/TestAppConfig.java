package ru.yandex.practicum.filmorate.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestAppConfig {

    @Bean
    public AppProperties appProperties() {
        return new AppProperties();
    }
}
