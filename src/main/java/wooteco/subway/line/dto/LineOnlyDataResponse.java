package wooteco.subway.line.dto;

import wooteco.subway.line.Line;

public class LineOnlyDataResponse {

    private Long id;
    private String name;
    private String color;

    public LineOnlyDataResponse() {
    }

    public LineOnlyDataResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor());
    }

    public LineOnlyDataResponse(Long id, String name, String color) {
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
