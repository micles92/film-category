package org.film.service;

import org.film.service.ranking.RankingData;
import org.film.dto.FilmDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FilmService {

    String addFilm(MultipartFile file, String title, String director, int productionYear);

    void updateFilmRankingByTitle(String title, int criticsRating);

    void addFilmRankingByTitle(String title, RankingData rankingData);

    Resource downloadFilmByTitle(String title);

    List<FilmDto> findAll(Sort sort);
}
