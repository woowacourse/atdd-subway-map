package wooteco.subway.dao.entity;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;

public class LineEntity {

    private final Long id;
    private final String name;
    private final String color;

    public LineEntity(final String name, final String color) {
        this(null, name, color);
    }

    public LineEntity(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineEntity from(final Line line) {
        return new LineEntity(line.getId(), line.getName(), line.getColor());
    }

    public Line toLine(final Sections sections) {
        return new Line(id, name, color, sections);
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
