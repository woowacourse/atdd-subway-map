package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineDto {

    private Long id;
    private String name;
    private String color;

    private LineDto() {
    }

    public LineDto(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineDto(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static LineDto from(Line line) {
        return new LineDto(line.getId(), line.getName(), line.getColor());
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
