package org.film.digikat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.digikat.response.RankingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DigiKatApiCaller {
    private final WebClient webClient;

    public Optional<RankingResponse> getRankingData(final String title) {
        try {
            return Optional.ofNullable(fetchRankingData(title));
        } catch (WebClientResponseException ex) {
            log.error("Client error occurred: status = {}, message = {}", ex.getStatusCode(), ex.getMessage());
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private RankingResponse fetchRankingData(String title) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/ranking")
                        .queryParam("film", title)
                        .build())
                .retrieve()
                .bodyToMono(RankingResponse.class)
                .block();
    }
}
