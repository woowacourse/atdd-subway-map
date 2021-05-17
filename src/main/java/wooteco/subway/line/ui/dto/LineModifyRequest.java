package wooteco.subway.line.ui.dto;

import java.beans.ConstructorProperties;

public class LineModifyRequest {
    private final String color;
    private final String name;

    @ConstructorProperties({"color", "name"})
    public LineModifyRequest(final String color, final String name) {
        this.color = color;
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
