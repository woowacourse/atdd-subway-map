package wooteco.subway.domain;

import wooteco.subway.exception.constant.BlankArgumentException;

import java.util.Objects;

import static wooteco.subway.util.StringUtils.isBlank;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id) {
        this(id, null, null);
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        if (isBlank(name) || isBlank(color)) {
            throw new BlankArgumentException();
        }
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
