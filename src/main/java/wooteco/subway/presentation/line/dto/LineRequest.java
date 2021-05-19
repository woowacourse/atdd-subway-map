package wooteco.subway.presentation.line.dto;

import wooteco.subway.presentation.valid.RightNumberInput;
import wooteco.subway.presentation.valid.RightStringInput;

import java.beans.ConstructorProperties;

public class LineRequest {
    @RightStringInput
    private final String name;
    @RightStringInput
    private final String color;
    @RightNumberInput
    private final Long upStationId;
    @RightNumberInput
    private final Long downStationId;
    @RightNumberInput
    private final Long distance;

    @ConstructorProperties({"name", "color", "upStationId", "downStationId", "distance"})
    public LineRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
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

    public Long getDistance() {
        return distance;
    }

}
