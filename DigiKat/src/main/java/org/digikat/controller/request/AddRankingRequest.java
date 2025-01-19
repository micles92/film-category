package org.digikat.controller.request;

import org.digikat.controller.response.RankingData;

public record AddRankingRequest(
        RankingData rankingData
) {
}
