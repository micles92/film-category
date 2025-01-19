package org.film.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.service.ranking.RankingData;
import org.film.dto.FilmDto;
import org.film.model.FilmEntity;
import org.film.repository.FilmRepository;
import org.film.service.file.FileManagementService;
import org.film.service.ranking.FilmRankingService;
import org.film.service.validation.ValidationService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmManager implements FilmService {

    private final ValidationService validationService;
    private final FilmRepository repository;
    private final FilmRankingService rankingService;
    private final FileManagementService fileManagementService;

    @Override
    public String addFilm(final MultipartFile file, final String title, final String director, final int productionYear) {
        validationService.validate(title, director, productionYear, file.getSize());

        String path = fileManagementService.storeFile(file, title, director, productionYear);

        FilmEntity filmEntity = FilmEntity.builder()
                .title(title)
                .director(director)
                .productionYear(productionYear)
                .fileSize(file.getSize())
                .path(path)
                .build();

        rankingService.enrichFilmWithRankingData(filmEntity, title);

        repository.save(filmEntity);
        log.info("Film with title '{}' successfully added.", title);

        return filmEntity.getId().toString();
    }

    @Override
    public void updateFilmRankingByTitle(final String title, final int criticsRating) {
        log.info("Updating ranking for film with title '{}'", title);
        FilmEntity filmEntity = repository.findByTitle(title)
                .orElseThrow(() -> new FilmNotFoundException("Film with title " + title + " not found."));
        rankingService.updateFilmRanking(filmEntity, criticsRating);
        repository.save(filmEntity);
        log.info("Ranking updated for film '{}'. New ranking: {}", title, filmEntity.getRanking());
    }

    @Override
    public void addFilmRankingByTitle(final String title, final RankingData rankingData) {
        log.info("Adding ranking for film with title '{}'", title);
        repository.findByTitle(title).ifPresentOrElse(
                filmEntity -> {
                    rankingService.addFilmRanking(filmEntity, rankingData);
                    repository.save(filmEntity);
                    log.info("Ranking added for film '{}'. New ranking: {}", title, filmEntity.getRanking());
                },
                () -> log.warn("Film with title '{}' not found.", title)
        );
    }

    @Override
    public Resource downloadFilmByTitle(final String title) {
        log.info("Starting film download process for title: '{}'", title);

        FilmEntity filmEntity = repository.findByTitle(title)
                .orElseThrow(() -> new FilmNotFoundException("Film with title " + title + " not found."));

        return fileManagementService.downloadFile(filmEntity.getPath(), title);
    }

    @Override
    public List<FilmDto> findAll(Sort sort) {
        return repository.findAll(sort).stream()
                .map(film ->
                        new FilmDto(film.getTitle(),
                                film.getDirector(),
                                film.getProductionYear(),
                                film.getRanking(),
                                film.getFileSize()))
                .toList();
    }
}
