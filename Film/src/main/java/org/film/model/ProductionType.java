package org.film.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public enum ProductionType {
    POLSKA_PISF(0),
    POLSKA_POZOSTALE(1),
    ZAGRANICZNA(2);

    private final int value;

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static ProductionType fromValue(int value) {
        for (ProductionType productionType : values()) {
            if (productionType.value == value) {
                return productionType;
            }
        }
        throw new IllegalArgumentException("Unknown production type: " + value);
    }
}
