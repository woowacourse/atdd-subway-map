package wooteco.subway.dto.response;

import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;

    private LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor());
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
