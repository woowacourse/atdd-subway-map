package wooteco.subway.line.domain;

import java.util.Objects;

public class LineColor {
    private final String color;

    public LineColor(String color) {
        this.color = color;
    }

    public String getColorName() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineColor lineColor = (LineColor) o;
        return Objects.equals(color, lineColor.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return color;
    }
}
