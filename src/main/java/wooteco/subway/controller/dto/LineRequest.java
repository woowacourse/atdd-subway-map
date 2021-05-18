package wooteco.subway.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public class LineRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String color;

    @NotNull
    private final Long upStationId;

    @NotNull
    private final Long downStationId;

    @Positive
    private final int distance;

    @JsonCreator
    public LineRequest(
            @JsonProperty(value = "name") final String name,
            @JsonProperty(value = "color") final String color,
            @JsonProperty(value = "upStationId") final Long upStationId,
            @JsonProperty(value = "downStationId") final Long downStationId,
            @JsonProperty(value = "distance") final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public Line toEntity(Section section) {
        return new Line(null, name, color, new Sections(section));
    }
}
