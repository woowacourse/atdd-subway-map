package wooteco.subway.domain;

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
}
