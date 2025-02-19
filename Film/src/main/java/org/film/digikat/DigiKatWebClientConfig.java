package org.film.digikat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class DigiKatWebClientConfig {

    @Value("${digiKat.api-url}")
    private String apiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.create(apiUrl);
    }
}
