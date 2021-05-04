package wooteco.subway.controller.dto.response;

import wooteco.subway.domain.Line;

public class LineResponseDto {
    private Long id;
    private String name;
    private String color;

    public LineResponseDto() {
    }

    public LineResponseDto(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponseDto(Line line) {
        this(line.getId(), line.getName(), line.getColor());
    }

    public LineResponseDto(Long id, Line line) {
        this(id, line.getName(), line.getColor());
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
