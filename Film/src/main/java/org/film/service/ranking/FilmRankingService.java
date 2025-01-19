package org.film.service.ranking;

import org.film.model.FilmEntity;

public interface FilmRankingService {

    void enrichFilmWithRankingData(FilmEntity filmEntity, String title);

    void updateFilmRanking(FilmEntity filmEntity, int criticsRating);

    void addFilmRanking(final FilmEntity filmEntity, final RankingData rankingData);
}
