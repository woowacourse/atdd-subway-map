package wooteco.subway.dto;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;

public class LineRequest {
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, long upStationId, long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line createLine() {
        return new Line(this.name, this.color, this.upStationId, this.downStationId);
    }

    public Section createSection() {
        return new Section(this.upStationId, this.downStationId, this.distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
