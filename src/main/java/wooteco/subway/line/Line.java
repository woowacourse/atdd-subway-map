package wooteco.subway.line;

import java.util.Objects;
import wooteco.subway.domain.Id;

public class Line {

    private Id id;
    private Name name;
    private Color color;

    public Line() {
    }

    public Line(final String name, final String color) {
        this(null, new Name(name), new Color(color));
    }

    public Line(final Long id, final String name, final String color) {
        this(new Id(id), new Name(name), new Color(color));
    }

    public Line(final Id id, final Name name, final Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        if (id == null) {
            return null;
        }
        return id.value();
    }

    public String getName() {
        return name.value();
    }

    public String getColor() {
        return color.value();
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
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
