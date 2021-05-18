package wooteco.subway.line.controller.dto;

import wooteco.subway.line.domain.Line;

public class LineNameColorResponse {
    private Long id;
    private String name;
    private String color;

    public LineNameColorResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineNameColorResponse from(Line line) {
        return new LineNameColorResponse(line.getId(), line.getName(), line.getColor());
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
