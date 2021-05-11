package wooteco.subway.dto;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
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

    public LineRequest(Line line, Section section) {
        this(line.getName(), line.getColor(), section.getUpStationId(), section.getDownStationId(),
            section.getDistance());
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

    public Section toSection() {
        return new Section(upStationId, downStationId, distance);
    }
}
