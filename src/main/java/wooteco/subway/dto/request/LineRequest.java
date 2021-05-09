package wooteco.subway.dto.request;

import wooteco.subway.domain.Line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class LineRequest {
    @NotBlank
    @Pattern(regexp = "^[가-힣|A-Z|a-z| 0-9]*선$")
    private String name;
    @NotBlank
    private String color;
    @NotBlank
    private Long upStationId;
    @NotBlank
    private Long downStationId;
    @NotBlank
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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

    public Line toEntity() {
        return new Line(name, color);
    }
}
