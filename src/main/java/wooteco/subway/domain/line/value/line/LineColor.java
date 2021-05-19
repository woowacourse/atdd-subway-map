package wooteco.subway.domain.line.value.line;

import wooteco.subway.exception.line.NameLengthException;

import java.util.Objects;

public final class LineColor {

    private final String lineColor;

    public LineColor(String lineColor) {
        validateLineColorSize(lineColor);
        this.lineColor = lineColor;
    }

    private void validateLineColorSize(String lineColor) {
        if(lineColor.isEmpty()) {
            throw new NameLengthException();
        }
    }

    public String asString() {
        return lineColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineColor lineColor1 = (LineColor) o;
        return Objects.equals(lineColor, lineColor1.lineColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineColor);
    }

}
