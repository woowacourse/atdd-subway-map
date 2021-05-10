package wooteco.subway.line.domain;

import wooteco.subway.section.domain.Sections;

import java.util.Objects;

public class Line {
    private final Long id;
    private final LineName name;
    private final LineColor color;
    private Sections sections;

    public Line(String name, String color) {
        this(null, name, color);
    }
    public Line(Long id, String name, String color) {
        this(id, new LineName(name), new LineColor(color));
    }

    public Line(Long id, LineName name, LineColor color) {
        this(id, name, color, null);
    }

    public Line(Long id, LineName name, LineColor color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public LineName getName() {
        return name;
    }

    public LineColor getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}