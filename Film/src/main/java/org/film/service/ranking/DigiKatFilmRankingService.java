package org.film.service.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.digikat.DigiKatApiCaller;
import org.film.model.FilmEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigiKatFilmRankingService implements FilmRankingService {

    private final DigiKatApiCaller apiCaller;
    private final RankingCalculatorService rankingCalculatorService;

    @Override
    public void enrichFilmWithRankingData(final FilmEntity filmEntity, final String title) {
        log.info("Attempting to enrich film with ranking data for title: {}", title);
        apiCaller.getRankingData(title).ifPresentOrElse(
                rankingDataResponse -> {
                    log.info("Received ranking data for '{}': {}", title, rankingDataResponse);
                    var rankingData = rankingDataResponse.rankingData();
                    int calculatedRanking = rankingCalculatorService.calculate(rankingData, filmEntity.getFileSize());

                    filmEntity.setCriticsRating(rankingData.criticsRating());
                    filmEntity.setRanking(calculatedRanking);
                    log.info("Successfully enriched film '{}' with ranking data.", title);
                },
                () -> log.warn("No ranking data found for the film with title '{}'.", title)
        );
    }

    @Override
    public void updateFilmRanking(final FilmEntity filmEntity, final int criticsRating) {
        log.info("Updating ranking for film with ID: {}", filmEntity.getId());
        int updatedRanking = rankingCalculatorService.reevaluate(
                filmEntity.getRanking(),
                criticsRating,
                filmEntity.getCriticsRating()
        );
        log.debug("Updated ranking for film ID {}: {}", filmEntity.getId(), updatedRanking);

        filmEntity.setCriticsRating(criticsRating);
        filmEntity.setRanking(updatedRanking);
        log.info("Film ranking updated for film ID: {}", filmEntity.getId());
    }

    @Override
    public void addFilmRanking(final FilmEntity filmEntity, final RankingData rankingData) {
        log.info("Adding ranking data for film ID: {}", filmEntity.getId());
        final int calculatedRanking = rankingCalculatorService.calculate(rankingData, filmEntity.getFileSize());
        filmEntity.setCriticsRating(rankingData.criticsRating());
        filmEntity.setRanking(calculatedRanking);
        log.info("Ranking data added for film ID: {} with critics ranking: {}", filmEntity.getId(), rankingData.criticsRating());
    }
}
