package wooteco.subway.dto;

import wooteco.subway.domain.line.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
    }

    public LineResponse(Long id, Line line) {
        this.id = id;
        this.name = line.getName();
        this.color = line.getColor();
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
