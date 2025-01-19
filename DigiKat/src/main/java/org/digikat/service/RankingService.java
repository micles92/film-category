package org.digikat.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.digikat.controller.request.AddRankingRequest;
import org.digikat.controller.response.RankingData;
import org.digikat.controller.response.RankingResponse;
import org.digikat.model.RankingEntity;
import org.digikat.rabbit.RankingMessageProducer;
import org.digikat.rabbit.events.AddRankingEvent;
import org.digikat.rabbit.events.UpdateRankingEvent;
import org.digikat.repository.RankingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingRepository rankingRepository;
    private final RankingMessageProducer producer;

    public void addRanking(AddRankingRequest request) {
        final RankingData rankingData = request.rankingData();
        final RankingEntity rankingToSave = RankingEntity.builder()
                .title(rankingData.title())
                .availablePlatforms(rankingData.availablePlatforms())
                .userRating(rankingData.userRating())
                .criticsRating(rankingData.criticsRating())
                .productionType(rankingData.production())
                .lastUpdate(LocalDate.now())
                .build();

        var addRankingEvent = new AddRankingEvent(rankingData);
        producer.sendAddedRankingEvent(addRankingEvent);
        rankingRepository.save(rankingToSave);
    }

    public RankingResponse getRankingByTitle(final String title) {
        return rankingRepository.findByTitle(title)
                .map(rank -> new RankingResponse(
                        new RankingData(
                                rank.getTitle(),
                                rank.getProductionType(),
                                rank.getAvailablePlatforms(),
                                rank.getUserRating(),
                                rank.getLastUpdate(),
                                rank.getCriticsRating()
                        )
                ))
                .orElseThrow(() -> new EntityNotFoundException("Ranking with title '%s' not found.".formatted(title)));
    }


    public RankingResponse updateRankingByTitle(final String title, int criticsRating) {
        var rank = rankingRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Ranking with title '%s' not found.".formatted(title)));

        rank.setCriticsRating(criticsRating);
        rank.setLastUpdate(LocalDate.now());

        var updatedRank = rankingRepository.save(rank);
        var updateRankingEvent = new UpdateRankingEvent(title, criticsRating);
        producer.sendUpdateRankingEvent(updateRankingEvent);

        return new RankingResponse(
                new RankingData(
                        updatedRank.getTitle(),
                        updatedRank.getProductionType(),
                        updatedRank.getAvailablePlatforms(),
                        updatedRank.getUserRating(),
                        updatedRank.getLastUpdate(),
                        updatedRank.getCriticsRating()
                )
        );
    }
}
