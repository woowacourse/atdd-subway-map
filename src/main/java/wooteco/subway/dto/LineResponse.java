package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;

    private LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse of(Long id, String name, String color) {
        return new LineResponse(id, name, color);
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
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
