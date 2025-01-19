package org.film.digikat;

import org.film.digikat.events.AddRankingEvent;
import org.film.digikat.events.UpdateRankingEvent;
import org.film.model.Platform;
import org.film.service.FilmManager;
import org.film.service.ranking.RankingData;
import org.film.service.ranking.RankingListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.List;

import static org.film.model.Platform.HBO;
import static org.film.model.ProductionType.ZAGRANICZNA;
import static org.film.model.UserRating.WYBITNY;
import static org.mockito.Mockito.*;

class RankingListenerTest {

    @InjectMocks
    private RankingListener rankingListener; // Klasa, którą testujemy

    @Mock
    private FilmManager filmManager; // Mockujemy FilmManager

    @Mock
    private Logger logger; // Mockujemy logger

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicjalizuje mocki przed każdym testem
    }

    @Test
    void testUpdateRankingListener() {
        // Przygotowanie danych wejściowych
        String title = "Film A";
        ; // Zaktualizowany typ ocen użytkowników
        LocalDate lastUpdate = LocalDate.now(); // Data ostatniej aktualizacji
        int criticsRating = 4;
        UpdateRankingEvent event = new UpdateRankingEvent(title, criticsRating);

        // Wywołanie metody listenera
        rankingListener.updateRankingListener(event);

        // Sprawdzenie, czy metoda updateFilmRankingByTitle została wywołana z odpowiednimi argumentami
        verify(filmManager, times(1)).updateFilmRankingByTitle(title, criticsRating);
    }

    @Test
    void testAddRankingListener_withValidData() {
        // Przygotowanie danych wejściowych
        RankingData rankingData = new RankingData("Film B", ZAGRANICZNA, List.of(HBO), WYBITNY, LocalDate.now(), 5);
        AddRankingEvent event = new AddRankingEvent(rankingData);

        // Wywołanie metody listenera
        rankingListener.addRankingListener(event);

        // Sprawdzenie, czy metoda addFilmRankingByTitle została wywołana z odpowiednimi argumentami
        verify(filmManager, times(1)).addFilmRankingByTitle(rankingData.title(), rankingData);
    }

    @Test
    void testAddRankingListener_withNullData() {
        // Przygotowanie danych wejściowych
        AddRankingEvent event = new AddRankingEvent(null);

        // Wywołanie metody listenera
        rankingListener.addRankingListener(event);

        // Sprawdzenie, czy filmManager nie został wywołany
        verify(filmManager, times(0)).addFilmRankingByTitle(anyString(), any());
    }

    @Test
    void testAddRankingListener_withEmptyData() {
        // Przygotowanie danych wejściowych
        AddRankingEvent event = new AddRankingEvent(new RankingData("", ZAGRANICZNA, List.of(Platform.NETFLIX), WYBITNY, LocalDate.now(), 0));

        // Wywołanie metody listenera
        rankingListener.addRankingListener(event);

        // Sprawdzenie, czy filmManager nie został wywołany
        verify(filmManager, times(0)).addFilmRankingByTitle(anyString(), any());
    }
}
