package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;

    public LineResponse() {
    }

    public LineResponse(Line newLine) {
        this.id = newLine.getId();
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
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
