package wooteco.subway.service.dto;

import wooteco.subway.domain.Line;

public class LineDto {
    private Long id;
    private String color;
    private String name;

    public LineDto() {

    }

    public LineDto(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public LineDto(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public LineDto(Line line) {
        this(line.getId(), line.getColor(), line.getName());
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
