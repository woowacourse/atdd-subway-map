package wooteco.subway.service.dto;

import wooteco.subway.domain.Line;

public class OnlyLineResponse {
    private final Long id;
    private final String name;
    private final String color;

    public OnlyLineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
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
