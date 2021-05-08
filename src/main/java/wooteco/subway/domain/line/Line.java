package wooteco.subway.domain.line;

import wooteco.subway.domain.section.Section;

import java.util.List;
import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;
    private List<Section> sections;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public boolean hasSameName(String name) {
        return this.name.equals(name);
    }

    public long getId() {
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
        return id == line.id && name.equals(line.name) && color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
