package wooteco.subway.ui.request;

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

    public Line toEntity() {
        return new Line(name, color);
    }

    public Line toEntity(Long id) {
        return new Line(id, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
