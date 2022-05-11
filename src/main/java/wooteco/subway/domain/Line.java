package wooteco.subway.domain;

import wooteco.subway.exception.ExceptionMessage;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        if (name.isBlank()) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_LINE_NAME.getContent());
        }
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
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
