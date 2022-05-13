package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.LineColorLengthException;

public class LineColor {

    private final static int MAX_COLOR_SIZE = 20;

    private final String value;

    public LineColor(final String value) {
        validateColor(value);
        this.value = value;
    }

    private void validateColor(final String color) {
        if (color.length() > MAX_COLOR_SIZE) {
            throw new LineColorLengthException("[ERROR] 노선 색은 20자 이하여야 합니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineColor)) {
            return false;
        }
        final LineColor lineColor = (LineColor) o;
        return Objects.equals(value, lineColor.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
