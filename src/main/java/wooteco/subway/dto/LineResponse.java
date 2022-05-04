package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(Line savedLine) {
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
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
