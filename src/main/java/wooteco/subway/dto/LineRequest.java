package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Line;

public class LineRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toEntity() {
        return new Line(name, color);
    }

    public Line toEntity(Long id) {
        return new Line(id, name, color);
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
}
