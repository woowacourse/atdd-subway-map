package wooteco.subway.service.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.ui.request.LineRequest;

public class LineDto {

    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public LineDto(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineDto from(LineRequest request) {
        return new LineDto(request.getName(), request.getColor(), request.getUpStationId(), request.getDownStationId(),
            request.getDistance());
    }

    public Line toLine() {
        return new Line(name, color);
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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
