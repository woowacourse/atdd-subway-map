package wooteco.subway.line.ui.dto;

import java.beans.ConstructorProperties;

public class LineRequest {
    private final String name;
    private final String color;

    @ConstructorProperties({"name", "color"})
    public LineRequest(final String name, final String color) {
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
