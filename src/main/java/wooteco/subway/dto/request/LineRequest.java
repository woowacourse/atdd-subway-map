package wooteco.subway.dto.request;

import wooteco.subway.entity.LineEntity;

public class LineRequest {

    private String name;
    private String color;

    private LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public LineEntity toEntity() {
        return LineEntity.of(name, color);
    }
}
