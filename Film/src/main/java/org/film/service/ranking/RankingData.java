package org.film.service.ranking;

import org.film.model.Platform;
import org.film.model.ProductionType;
import org.film.model.UserRating;

import java.time.LocalDate;
import java.util.List;

public record RankingData(
    String title,
    ProductionType production,
    List<Platform> availablePlatforms,
    UserRating userRating,
    LocalDate lastUpdate,
    int criticsRating
) {
}
