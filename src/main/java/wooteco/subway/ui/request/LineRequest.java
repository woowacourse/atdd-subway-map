package wooteco.subway.ui.request;

import wooteco.subway.domain.LineEntity;

public class LineRequest {

    private String name;
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineEntity toEntity() {
        return new LineEntity(name, color);
    }

    public LineEntity toEntity(Long id) {
        return new LineEntity(id, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
