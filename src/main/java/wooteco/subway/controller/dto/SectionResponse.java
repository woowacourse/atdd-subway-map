package wooteco.subway.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wooteco.subway.domain.Section;

public class SectionResponse {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionResponse(Long lineId, Section section) {
        this(section.getId(), lineId, section.getUpStation().getId(), section.getDownStation()
                                                                             .getId(), section.getDistance());
    }

    @JsonCreator
    public SectionResponse(
            @JsonProperty(value = "id") final Long id,
            @JsonProperty(value = "lineId") final Long lineId,
            @JsonProperty(value = "upStationId") final Long upStationId,
            @JsonProperty(value = "downStationId") final Long downStationId,
            @JsonProperty(value = "distance") final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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
