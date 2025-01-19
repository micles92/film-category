package org.film.utils;

public class DigikatResponseUtils {

    public static String createValidRankingJsonResponse(String title, int production, String userRating, String lastUpdate, int criticsRating) {
        return String.format("""
                {
                   "rankingData": {
                     "title": "%s",
                     "production": %d,
                     "availablePlatforms": ["NETFLIX"],
                     "userRating": "%s",
                     "lastUpdate": "%s",
                     "criticsRating": %d
                   }
                }
                """, title, production, userRating, lastUpdate, criticsRating);
    }
}
