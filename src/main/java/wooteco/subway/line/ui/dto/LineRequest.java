package wooteco.subway.line.ui.dto;

import java.beans.ConstructorProperties;

public class LineRequest {
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
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
