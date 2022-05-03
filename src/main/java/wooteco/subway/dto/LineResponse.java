package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(Line line) {
        Long id = line.getId();
        String name = line.getName();
        String color = line.getColor();

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
