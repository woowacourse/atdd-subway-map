package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Id id;
    private Name name;
    private Color color;

    public Line() {
    }

    public Line(String name, String color) {
        this(null, new Name(name), new Color(color));
    }

    public Line(Long id, String name, String color) {
        this(new Id(id), new Name(name), new Color(color));
    }

    public Line(Id id, Name name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean isDuplicate(Line line) {
        return name.equals(line.name) || color.equals(line.color);
    }

    public Long getId() {
        return id.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
            && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
