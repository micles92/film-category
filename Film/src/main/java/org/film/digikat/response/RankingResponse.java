package org.film.digikat.response;

import org.film.service.ranking.RankingData;

public record RankingResponse(
        RankingData rankingData
) {
}
