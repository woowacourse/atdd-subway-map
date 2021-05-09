package wooteco.subway.line.dto;

import wooteco.subway.line.Line;

public class LineRequest {

    private String name;
    private String color;

    public LineRequest() {
    }

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

    public Line lineWithoutId() {
        return Line.of(name, color);
    }
}

