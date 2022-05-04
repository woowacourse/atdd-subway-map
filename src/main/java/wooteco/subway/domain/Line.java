package wooteco.subway.domain;

import wooteco.subway.exception.BlankArgumentException;

public class Line {

    private Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        if (name.isBlank() || color.isBlank()) {
            throw new BlankArgumentException();
        }
        this.name = name;
        this.color = color;
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
