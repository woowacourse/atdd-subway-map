package wooteco.subway.dto;

import javax.validation.constraints.NotNull;
import wooteco.subway.domain.line.Line;

public class LineRequest {

    @NotNull
    private String name;
    @NotNull
    private String color;
    @NotNull(message = "생성할 구간 정보를 입력해주세요.")
    private Long upStationId;
    @NotNull(message = "생성할 구간 정보를 입력해주세요.")
    private Long downStationId;
    @NotNull(message = "생성할 구간 정보를 입력해주세요.")
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

    public Line toLine(Long id) {
        return new Line(id, name, color);
    }

    public Line toLine() {
        return new Line(name, color);
    }
}
