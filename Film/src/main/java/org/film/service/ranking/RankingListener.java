package org.film.service.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.digikat.events.AddRankingEvent;
import org.film.digikat.events.UpdateRankingEvent;
import org.film.service.FilmManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@Slf4j
@RequiredArgsConstructor
public class RankingListener {
    private final FilmManager filmManager;

    @RabbitListener(queues = "update-ranking")
    public void updateRankingListener(UpdateRankingEvent event) {
        log.info("Received update ranking event: {}", event);
        filmManager.updateFilmRankingByTitle(event.title(), event.criticsRating());
    }

    @RabbitListener(queues = "add-ranking")
    public void addRankingListener(AddRankingEvent event) {
        final RankingData rankingData = event.rankingData();
        log.info("Received add ranking event: {}", event);
        if (isNull(rankingData) || rankingData.title().isEmpty()) {
            log.warn("Received add ranking event is empty or invalid.");
            return;
        }
        filmManager.addFilmRankingByTitle(rankingData.title(), rankingData);
    }
}
