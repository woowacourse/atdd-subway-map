package wooteco.subway.controller.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String color;

    @NotNull
    private final Long upStationId;

    @NotNull
    private final Long downStationId;

    @NotNull
    @Positive
    private final int distance;

    public LineRequest(final String name, final String color, final Long upStationId, final Long downStationId,
                       final int distance) {
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

    public Line toLineEntity() {
        return new Line(null, name, color, upStationId, downStationId);
    }

    public Section toSectionEntity() {
        return new Section(null, null, upStationId, downStationId, distance);
    }
}
