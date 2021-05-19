package wooteco.subway.domain.line.value.line;

import wooteco.subway.exception.line.NegativeIdException;

import java.util.Objects;

public final class LineId extends Number {

    private final Long id;

    private LineId() {
        this.id = -1L;
    }

    public LineId(Long id) {
        validateThatIsNegativeNumber(id);
        this.id = id;
    }

    private void validateThatIsNegativeNumber(Long id) {
        if(id < 0) {
            throw new NegativeIdException();
        }
    }

    public static LineId empty() {
        return new LineId();
    }

    @Override
    public int intValue() {
        return id.intValue();
    }

    @Override
    public long longValue() {
        return id.longValue();
    }

    @Override
    public float floatValue() {
        return id.floatValue();
    }

    @Override
    public double doubleValue() {
        return id.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineId lineId = (LineId) o;
        return Objects.equals(id, lineId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
