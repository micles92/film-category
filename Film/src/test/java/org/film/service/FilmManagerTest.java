package org.film.service;

import org.film.service.ranking.RankingData;
import org.film.dto.FilmDto;
import org.film.model.FilmEntity;
import org.film.repository.FilmRepository;
import org.film.service.file.FileManagementService;
import org.film.service.ranking.FilmRankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.film.model.Platform.HBO;
import static org.film.model.ProductionType.ZAGRANICZNA;
import static org.film.model.UserRating.WYBITNY;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmManagerTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private FilmRankingService rankingService;

    @Mock
    private FileManagementService fileManagementService;

    @InjectMocks
    private FilmManager filmManager;

    private FilmEntity filmEntity;

    @BeforeEach
    void setUp() {
        // Given
        filmEntity = new FilmEntity();
        filmEntity.setId(UUID.randomUUID());
        filmEntity.setTitle("Film Title");
        filmEntity.setDirector("Director Name");
        filmEntity.setProductionYear(2025);
        filmEntity.setFileSize(1024L);
        filmEntity.setPath("path/to/film");
    }

    @Test
    void shouldAddFilmRankingByTitle() {
        // Given
        String title = "Film Title";
        LocalDate lastUpdate = LocalDate.now();
        RankingData rankingData = new RankingData(title, ZAGRANICZNA, List.of(HBO), WYBITNY, lastUpdate, 10);
        when(filmRepository.findByTitle(title)).thenReturn(Optional.of(filmEntity));

        // When
        filmManager.addFilmRankingByTitle(title, rankingData);

        // Then
        verify(rankingService, times(1)).addFilmRanking(filmEntity, rankingData);
    }

    @Test
    void shouldDownloadFilmByTitle() {
        // Given
        String title = "Film Title";
        when(filmRepository.findByTitle(title)).thenReturn(Optional.of(filmEntity));
        Resource resource = mock(Resource.class);
        when(fileManagementService.downloadFile(filmEntity.getPath(), title)).thenReturn(resource);

        // When
        Resource result = filmManager.downloadFilmByTitle(title);

        // Then
        assertThat(result).isEqualTo(resource);
    }

    @Test
    void shouldReturnAllFilmsSorted() {
        // Given
        Sort sort = Sort.by(Sort.Order.asc("title"));
        when(filmRepository.findAll(sort)).thenReturn(List.of(filmEntity));

        // When
        List<FilmDto> films = filmManager.findAll(sort);

        // Then
        assertThat(films).isNotEmpty();
        assertThat(films.get(0).title()).isEqualTo(filmEntity.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenFilmNotFoundForRankingUpdate() {
        // Given
        String title = "Non Existing Film";
        int criticsRating = 5;
        when(filmRepository.findByTitle(title)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> filmManager.updateFilmRankingByTitle(title, criticsRating))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessageContaining("Film with title Non Existing Film not found.");
    }

    @Test
    void shouldUpdateFilmRankingWhenFilmExists() {
        // Given
        String title = "Film Title";
        int criticsRating = 8;
        when(filmRepository.findByTitle(title)).thenReturn(Optional.of(filmEntity));

        // When
        filmManager.updateFilmRankingByTitle(title, criticsRating);

        // Then
        verify(filmRepository, times(1)).save(filmEntity);
        verify(rankingService, times(1)).updateFilmRanking(filmEntity, criticsRating);
    }


    @Test
    void shouldThrowExceptionWhenFilmNotFoundForDownload() {
        // Given
        String title = "Non Existing Film";
        when(filmRepository.findByTitle(title)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> filmManager.downloadFilmByTitle(title))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessageContaining("Film with title Non Existing Film not found.");
    }
}
