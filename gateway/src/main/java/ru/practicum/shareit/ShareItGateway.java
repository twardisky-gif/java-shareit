package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка запуска сервиса валидации и маршрутизации запросов.
 */
@SpringBootApplication
public class ShareItGateway {

    public static void main(String[] args) {
        SpringApplication.run(ShareItGateway.class, args);
    }
}
