package org.digikat.repository;

import org.digikat.model.RankingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RankingRepository extends JpaRepository<RankingEntity, UUID> {

    Optional<RankingEntity> findByTitle(final String title);
}
