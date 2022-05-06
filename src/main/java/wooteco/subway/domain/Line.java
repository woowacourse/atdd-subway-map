package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private Name name;
    private String color;

    public Line(final String name, final String color) {
        this.name = new Name(name);
        this.color = color;
    }

    public void updateName(final String name) {
        this.name = new Name(name);
    }

    public void updateColor(final String color) {
        this.color = color;
    }

    public boolean isSameName(final Line line) {
        return name.equals(line.name);
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
