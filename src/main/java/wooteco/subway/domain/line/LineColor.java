package wooteco.subway.domain.line;

import java.util.Objects;

public class LineColor {

    private final String color;

    public LineColor(String color) {
        validateColorNotBlank(color);
        this.color = color;
    }

    private void validateColorNotBlank(String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("지하철노선 색상은 공백이 될 수 없습니다.");
        }
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineColor lineColor = (LineColor) o;
        return Objects.equals(color, lineColor.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
