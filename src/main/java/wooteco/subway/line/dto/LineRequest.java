package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;

public class LineRequest {
    private String name;
    private String color;
    private Long firstStationId;
    private Long lastStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color, final Long firstStationId, final Long lastStationId, final int distance) {
        this.name = name;
        this.color = color;
        this.firstStationId = firstStationId;
        this.lastStationId = lastStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public Long getLastStationId() {
        return lastStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(null, name, color, firstStationId, lastStationId);
    }

    public Line toLine(final Long id) {
        return new Line(id, name, color, firstStationId, lastStationId);
    }
}
