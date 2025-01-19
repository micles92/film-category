package org.film.service;

import org.film.service.ranking.RankingData;
import org.film.service.ranking.RankingCalculatorService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.film.model.Platform.NETFLIX;
import static org.film.model.Platform.UNKNOWN;
import static org.film.model.ProductionType.POLSKA_PISF;
import static org.film.model.ProductionType.POLSKA_POZOSTALE;
import static org.film.model.UserRating.DOBRY;
import static org.film.model.UserRating.WYBITNY;

class RankingCalculatorServiceTest {

    private final RankingCalculatorService rankingCalculatorService = new RankingCalculatorService();

    @ParameterizedTest
    @MethodSource("provideFilmDataAndExpectedRanking")
    void shouldCalculateCorrectRanking(RankingData rankingData, long fileSize, int expectedRanking) {
        // When
        int actualRanking = rankingCalculatorService.calculate(rankingData, fileSize);

        // Then
        assertThat(actualRanking).isEqualTo(expectedRanking);
    }

    private static Stream<Arguments> provideFilmDataAndExpectedRanking() {
        return Stream.of(
                Arguments.of(new RankingData("Film 1", POLSKA_PISF, List.of(NETFLIX), WYBITNY, LocalDate.of(2025, 1, 17), 50), 150_000_000L, 100), // film small -> ranking = 100
                Arguments.of(new RankingData("Film 2", POLSKA_POZOSTALE, List.of(UNKNOWN), DOBRY, LocalDate.of(2025, 1, 17), 30), 150_000_000L, 100), // film small -> ranking = 100
                Arguments.of(new RankingData("Film 3", null, List.of(), null, LocalDate.of(2025, 1, 17), 0), 0L, 100), // film small -> ranking = 100
                Arguments.of(new RankingData("Film 4", POLSKA_PISF, List.of(NETFLIX, UNKNOWN), WYBITNY, LocalDate.of(2025, 1, 17), 50), 150_000_000L, 100), // film small -> ranking = 100
                Arguments.of(new RankingData("Film 5", POLSKA_PISF, List.of(NETFLIX), WYBITNY, LocalDate.of(2025, 1, 17), 50), 250_000_000L, 300), // film larger than SMALL_FILM_SIZE_LIMIT_BYTES -> normal calculation
                Arguments.of(new RankingData("Film 6", POLSKA_PISF, List.of(NETFLIX), WYBITNY, LocalDate.of(2025, 1, 17), 50), 200_000_000L, 100)  // film equal to SMALL_FILM_SIZE_LIMIT_BYTES -> ranking = 100
        );
    }

    @ParameterizedTest
    @MethodSource("provideReevaluateDataAndExpectedRanking")
    void shouldReevaluateCorrectly(int currentRanking, int previousCriticsRating, int currentCriticsRating, int expectedNewRanking) {
        // When
        int newRanking = rankingCalculatorService.reevaluate(currentRanking, currentCriticsRating, previousCriticsRating);

        // Then
        assertThat(newRanking).isEqualTo(expectedNewRanking);
    }

    private static Stream<Arguments> provideReevaluateDataAndExpectedRanking() {
        return Stream.of(
                Arguments.of(200, 100, 50, 150),
                Arguments.of(250, 80, 80, 250),
                Arguments.of(300, 150, 100, 250)
        );
    }
}
