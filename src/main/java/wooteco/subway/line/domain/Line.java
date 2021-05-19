package wooteco.subway.line.domain;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Long firstStationId;
    private final Long lastStationId;

    public Line(final Long id, final String name, final String color, final Long firstStationId, final Long lastStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.firstStationId = firstStationId;
        this.lastStationId = lastStationId;
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

    public Long getFirstStationId() {
        return firstStationId;
    }

    public Long getLastStationId() {
        return lastStationId;
    }
}
