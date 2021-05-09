package wooteco.subway.controller.dto;

import wooteco.subway.domain.Line;

public class LineRequest {
    private final String name;
    private final String color;

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toEntity() {
        return new Line(null, name, color);
    }
}
