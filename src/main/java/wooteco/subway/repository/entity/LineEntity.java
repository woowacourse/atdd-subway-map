package wooteco.subway.repository.entity;

import wooteco.subway.domain.Line;

public class LineEntity {

    private final Long id;
    private final String name;
    private final String color;

    public LineEntity(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineEntity(final Line line) {
        this(line.getId(), line.getName(), line.getColor());
    }

    public Line generateLine() {
        return new Line(id, name, color);
    }

    public LineEntity fillId(final Long id) {
        return new LineEntity(id, name, color);
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
}
