package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Line;

public class LineRequest {

    @NotBlank(message = "노선 이름은 빈 값일 수 없습니다.")
    private final String name;
    @NotBlank(message = "노선 색상은 빈 값일 수 없습니다.")
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    private LineRequest() {
        this(null, null, null, null, 0);
    }

    public LineRequest(String name, String color) {
        this(name, color, null, null, 0);
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
