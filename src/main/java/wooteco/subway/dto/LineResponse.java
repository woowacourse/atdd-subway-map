package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;

    private LineResponse() {
    }

    private LineResponse(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor()
        );
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
