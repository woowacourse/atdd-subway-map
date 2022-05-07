package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.LineNameEmptyException;
import wooteco.subway.exception.StationColorEmptyException;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new LineNameEmptyException();
        }
    }

    private void validateColor(final String color) {
        if (color.isBlank()) {
            throw new StationColorEmptyException();
        }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
