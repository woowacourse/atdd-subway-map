package wooteco.subway.ui.request;

import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

public class LineRequest {

    private int distance;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;

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

    public Line toLine(Long id) {
        return new Line(id, name, color);
    }

    public LineDto toLineDto() {
        return new LineDto(name, color, upStationId, downStationId, distance);
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
