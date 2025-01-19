package org.digikat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    int productionType;

    @ElementCollection(targetClass = Platform.class)
    List<Platform> availablePlatforms;

    UserRating userRating;

    int criticsRating;

    LocalDate lastUpdate;
}
