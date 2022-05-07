package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;

    public LineResponse() {
    }

    public LineResponse(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(final Line line) {
        final Long id = line.getId();
        final String name = line.getName();
        final String color = line.getColor();

        return new LineResponse(id, name, color);
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
