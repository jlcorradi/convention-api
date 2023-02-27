package dev.jlcorradi.convention;

import dev.jlcorradi.convention.core.service.ConventionSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ConventionApiApplication {

    private final ConventionSessionService conventionSessionService;

    public static void main(String[] args) {
        SpringApplication.run(ConventionApiApplication.class, args);
    }

    @Bean
    CommandLineRunner appAfterInit() {
        return args -> {
            conventionSessionService.startupSessionsCheck();
        };
    }

}
