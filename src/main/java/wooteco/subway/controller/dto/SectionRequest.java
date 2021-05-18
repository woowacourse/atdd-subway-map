package wooteco.subway.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SectionRequest {

    @NotNull
    private final Long upStationId;

    @NotNull
    private final Long downStationId;

    @Positive
    private final int distance;

    @JsonCreator
    public SectionRequest(
            @JsonProperty(value = "upStationId") final Long upStationId,
            @JsonProperty(value = "downStationId") final Long downStationId,
            @JsonProperty(value = "distance") final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
