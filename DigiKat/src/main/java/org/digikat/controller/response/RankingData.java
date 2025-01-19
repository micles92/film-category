package org.digikat.controller.response;

import org.digikat.model.Platform;
import org.digikat.model.UserRating;

import java.time.LocalDate;
import java.util.List;

public record RankingData(String title,
                          int production,
                          List<Platform> availablePlatforms,
                          UserRating userRating,
                          LocalDate lastUpdate,
                          int criticsRating) {
}
