package wooteco.subway.line.domain;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(final Long id, final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(final String name, final String color, final Long upStationId, final Long downStationId) {
        this(null, name, color, upStationId, downStationId);
    }

    public Line(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        this(null, name, color, upStationId, downStationId, distance);
    }

    public Line(final Long id, final String name, final String color, final Long upStationId, final Long downStationId) {
        this(id, name, color, upStationId, downStationId, 0);
    }

    public Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean isDifferentName(final Line line) {
        return !name.equals(line.name);
    }

    public Long getId() {
        return id;
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
