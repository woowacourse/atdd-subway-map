package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineRequest {

    private String name;
    private String color;

    private LineRequest() {
    }

    private LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
