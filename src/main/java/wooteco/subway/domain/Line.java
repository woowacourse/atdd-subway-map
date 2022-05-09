package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.ExceptionMessage;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        if (name.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_LINE_NAME.getContent());
        }
        this.name = name;
        this.color = color;
    }

    public void update(final Line line) {
        this.name = line.name;
        this.color = line.color;
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
        return name;
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
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
