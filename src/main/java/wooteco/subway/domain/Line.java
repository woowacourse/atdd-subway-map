package wooteco.subway.domain;

import wooteco.subway.exception.LineColorException;
import wooteco.subway.exception.LineNameException;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(final Long id, final String name, final String color) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new LineNameException();
        }
    }

    private void validateColor(final String color) {
        if (color.isBlank()) {
            throw new LineColorException();
        }
    }

    public Line(final String name, final String color) {
        this(null, name, color);
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
}
