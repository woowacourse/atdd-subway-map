package wooteco.subway.dao.dto;

import wooteco.subway.domain.Line;

public class LineDto {

    private final String name;
    private final String color;

    public LineDto(Line line) {
        this(line.getName(), line.getColor());
    }

    public LineDto(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

