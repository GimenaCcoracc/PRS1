package pe.edu.vallegrande.attendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient personWebClient() {
        return WebClient.builder()
                .baseUrl("https://vg-ms-person.onrender.com/api/v1/person/active")
                .build();
    }
}