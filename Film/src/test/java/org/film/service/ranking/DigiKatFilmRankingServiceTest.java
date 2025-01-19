package org.film.service.ranking;

import org.film.digikat.DigiKatApiCaller;
import org.film.digikat.response.RankingResponse;
import org.film.model.FilmEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.film.model.Platform.HBO;
import static org.film.model.ProductionType.ZAGRANICZNA;
import static org.film.model.UserRating.WYBITNY;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DigiKatFilmRankingServiceTest {

    @Mock
    private DigiKatApiCaller apiCaller;

    @Mock
    private RankingCalculatorService rankingCalculatorService;

    @InjectMocks
    private DigiKatFilmRankingService digiKatFilmRankingService;

    private FilmEntity filmEntity;

    @BeforeEach
    void setUp() {
        filmEntity = new FilmEntity();
        filmEntity.setId(UUID.randomUUID());
        filmEntity.setTitle("Film Title");
        filmEntity.setFileSize(1024L);
    }

    @Test
    void shouldEnrichFilmWithRankingDataWhenApiReturnsData() {
        // Given
        String title = "Film Title";
        RankingData rankingData = new RankingData(title, ZAGRANICZNA, List.of(HBO), WYBITNY, null, 9);
        when(apiCaller.getRankingData(title)).thenReturn(Optional.of(new RankingResponse(rankingData)));
        when(rankingCalculatorService.calculate(any(), anyLong())).thenReturn(85);

        // When
        digiKatFilmRankingService.enrichFilmWithRankingData(filmEntity, title);

        // Then
        assertThat(filmEntity.getCriticsRating()).isEqualTo(9);
        assertThat(filmEntity.getRanking()).isEqualTo(85);
        verify(apiCaller, times(1)).getRankingData(title);
        verify(rankingCalculatorService, times(1)).calculate(any(), eq(1024L));
    }

    @Test
    void shouldNotEnrichFilmWithRankingDataWhenApiReturnsEmpty() {
        // Given
        String title = "Film Title";
        when(apiCaller.getRankingData(title)).thenReturn(Optional.empty());

        // When
        digiKatFilmRankingService.enrichFilmWithRankingData(filmEntity, title);

        // Then
        assertThat(filmEntity.getCriticsRating()).isEqualTo(0);
        assertThat(filmEntity.getRanking()).isEqualTo(0);
        verify(apiCaller, times(1)).getRankingData(title);
        verify(rankingCalculatorService, times(0)).calculate(any(), anyLong());
    }

    @Test
    void shouldUpdateFilmRanking() {
        // Given
        int criticsRating = 9;
        int initialRanking = 75;
        filmEntity.setRanking(initialRanking);
        filmEntity.setCriticsRating(7);
        when(rankingCalculatorService.reevaluate(anyInt(), anyInt(), anyInt())).thenReturn(80);

        // When
        digiKatFilmRankingService.updateFilmRanking(filmEntity, criticsRating);

        // Then
        assertThat(filmEntity.getCriticsRating()).isEqualTo(9);
        assertThat(filmEntity.getRanking()).isEqualTo(80);
        verify(rankingCalculatorService, times(1)).reevaluate(initialRanking, criticsRating, 7);
    }

    @Test
    void shouldAddFilmRanking() {
        // Given
        RankingData rankingData = new RankingData("Film Title", null, null, WYBITNY, null, 9);
        when(rankingCalculatorService.calculate(any(), anyLong())).thenReturn(85);

        // When
        digiKatFilmRankingService.addFilmRanking(filmEntity, rankingData);

        // Then
        assertThat(filmEntity.getCriticsRating()).isEqualTo(9);
        assertThat(filmEntity.getRanking()).isEqualTo(85);
        verify(rankingCalculatorService, times(1)).calculate(rankingData, 1024L);
    }
}
