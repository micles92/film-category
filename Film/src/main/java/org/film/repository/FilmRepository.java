package org.film.repository;


import org.film.model.FilmEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FilmRepository extends JpaRepository<FilmEntity, UUID> {
    Optional<FilmEntity> findByTitle(final String title);

    List<FilmEntity> findAll(Sort sort);
}
