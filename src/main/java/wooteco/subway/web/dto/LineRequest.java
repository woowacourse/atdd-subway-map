package wooteco.subway.web.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import wooteco.subway.domain.line.Line;

public class LineRequest {

    @NotEmpty
    private String name;
    @NotEmpty
    private String color;
    @Min(1)
    private Long upStationId;
    @Min(1)
    private Long downStationId;
    @Min(0)
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId,
            int distance) {
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
