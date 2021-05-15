package wooteco.subway.domain;

import jdk.nashorn.internal.ir.ReturnNode;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Long topStationId;
    private final Long bottomStationId;

    public Line(final Long id, final Line line) {
        this(id, line.name, line.color, line.topStationId, line.bottomStationId);
    }

    public Line(final Long id, final String name, final String color, final Long topStationId,
                final Long bottomStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.topStationId = topStationId;
        this.bottomStationId = bottomStationId;
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

    public Long getTopStationId() {
        return topStationId;
    }

    public Long getBottomStationId() {
        return bottomStationId;
    }

    public Line updateTopStationId(final Long topStationId) {
        return new Line(id, name, color, topStationId, bottomStationId);
    }

    public Line updateBottomStationId(final Long bottomStationId) {
        return new Line(id, name, color, topStationId, bottomStationId);
    }

    public boolean isNameEquals(Line line) {
        return this.name.equals(line.name);
    }

    public boolean isTopStationIdEquals(Long topStationId) {
        return this.topStationId.equals(topStationId);
    }

    public boolean isBottomStationIdEquals(Long bottomStationId) {
        return this.bottomStationId.equals(bottomStationId);
    }
}
