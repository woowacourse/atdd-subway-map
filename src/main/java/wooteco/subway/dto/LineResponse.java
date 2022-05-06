package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    private LineResponse() {
    }

    public LineResponse(final Line line) {
        this.id = line.getId();
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
