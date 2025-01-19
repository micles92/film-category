package org.film.service.ranking;

public interface RankingCalculator {
    int calculate(RankingData rankingData, long fileSize);

    int reevaluate(int ranking, int currentCriticsRating, int previousCriticsRating);
}
