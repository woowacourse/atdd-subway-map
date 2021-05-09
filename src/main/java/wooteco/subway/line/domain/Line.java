package wooteco.subway.line.domain;

import wooteco.subway.section.domain.Section;

import java.util.Objects;

public class Line {
    private Long id;
    private final String color;
    private final String name;
    private Section section;

    public Line(final String color, final String name) {
        this.color = color;
        this.name = name;
    }

    public Line(final String color, final String name, final Section section) {
        this.color = color;
        this.name = name;
        this.section = section;
    }

    public Line(final Long id, final String color, final String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(final Long id, final String color, final String name, final Section section) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(color, line.color) && Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, name);
    }
}
