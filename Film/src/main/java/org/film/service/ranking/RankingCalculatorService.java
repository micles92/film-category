package org.film.service.ranking;

import lombok.extern.slf4j.Slf4j;
import org.film.model.Platform;
import org.film.model.ProductionType;
import org.film.model.UserRating;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.film.model.Platform.NETFLIX;
import static org.film.model.ProductionType.*;
import static org.film.model.UserRating.*;

@Slf4j
@Service
public class RankingCalculatorService implements RankingCalculator {
    static long SMALL_FILM_SIZE_LIMIT_BYTES = 200_000_000L;

    public static final int X = 200000000;
    private static final Map<ProductionType, Integer> PRODUCTION_RANKING_MAP = Map.of(
            POLSKA_PISF, 200,
            POLSKA_POZOSTALE, 200,
            ZAGRANICZNA, 0
    );

    private static final Map<Platform, Integer> PLATFORM_PENALTY_MAP = Map.of(
            NETFLIX, -50
    );

    private static final Map<UserRating, Integer> USER_RATING_BONUS_MAP = Map.of(
            WYBITNY, 100,
            DOBRY, 0,
            MIERNY, 0
    );

    @Override
    public int calculate(RankingData rankingData, long fileSize) {
        if (isSmallFilm(fileSize)) {
            return 100;
        }
        final int criticsRating = Math.max(rankingData.criticsRating(), 0);
        final int calculatedRanking = criticsRating
                + calculateProductionBonus(rankingData.production())
                + calculatePlatformPenalties(rankingData.availablePlatforms())
                + calculateUserRatingBonus(rankingData.userRating());
        log.debug("Calculated ranking: {}", calculatedRanking);
        return calculatedRanking;
    }

    @Override
    public int reevaluate(int ranking, int currentCriticsRating, int previousCriticsRating) {
        int adjustedCurrentCriticsRating = Math.max(currentCriticsRating, 0);
        int adjustedPreviousCriticsRating = Math.max(previousCriticsRating, 0);

        return ranking - adjustedPreviousCriticsRating + adjustedCurrentCriticsRating;
    }


    private int calculateProductionBonus(ProductionType productionType) {
        if (isNull(productionType)) {
            return 0;
        }
        return PRODUCTION_RANKING_MAP.getOrDefault(productionType, 0);
    }

    private int calculatePlatformPenalties(List<Platform> platforms) {
        return platforms.stream()
                .mapToInt(platform -> PLATFORM_PENALTY_MAP.getOrDefault(platform, 0))
                .sum();
    }

    private int calculateUserRatingBonus(UserRating userRating) {
        if (isNull(userRating)) {
            return 0;
        }
        return USER_RATING_BONUS_MAP.getOrDefault(userRating, 0);
    }

    private boolean isSmallFilm(final long filmSize) {
        return filmSize <= SMALL_FILM_SIZE_LIMIT_BYTES;
    }
}
