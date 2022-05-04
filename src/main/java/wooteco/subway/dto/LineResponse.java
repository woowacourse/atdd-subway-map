package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;

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
