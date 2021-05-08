package wooteco.subway.line.domain;

import java.util.Objects;

public class Line {
    private Long id;
    private LineName name;
    private LineColor color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this(id, new LineName(name), new LineColor(color));
    }

    public Line(Long id, LineName name, LineColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name=" + name +
                ", color=" + color +
                '}';
    }
}

