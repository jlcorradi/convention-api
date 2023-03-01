package dev.jlcorradi.convention;

import dev.jlcorradi.convention.core.service.ConventionSessionService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info = @Info(
                title = "Convention API",
                version = "1.0.0",
                description = "Convention voting management System - Developer skills evaluation" +
                        "For Jorge Corradi",
                license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0")
        ),
        servers = @Server(
                url = "http://localhost:8080",
                description = "Local server"
        )
)
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
