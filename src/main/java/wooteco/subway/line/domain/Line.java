package wooteco.subway.line.domain;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;

    public Line(final Long id, final String name, final String color, final Long upStationId, final Long downStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
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
}
