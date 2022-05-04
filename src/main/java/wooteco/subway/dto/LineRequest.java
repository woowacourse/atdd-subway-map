package wooteco.subway.dto;

import wooteco.subway.domain.Line;

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

    public Line toEntity() {
        return new Line(this.name, this.color);
    }
}
