package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

public class LineRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*선$", message = "지하철 노선 이름이 잘못되었습니다.")
    private String name;
    private String color;
    @Positive(message = "상행역 입력값이 올바르지 않습니다.")
    private Long upStationId;
    @Positive(message = "하행역 입력값이 올바르지 않습니다.")
    private Long downStationId;
    @Positive(message = "구간의 거리는 양수만 가능합니다.")
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

    public Line toLineEntity() {
        return new Line(null, name, color);
    }
}
