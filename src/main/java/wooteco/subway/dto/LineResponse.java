package wooteco.subway.dto;

import wooteco.subway.domain.Line;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;

    public LineResponse(Line line){
        id = line.getId();
        name = line.getName();
        color = line.getColor();
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
