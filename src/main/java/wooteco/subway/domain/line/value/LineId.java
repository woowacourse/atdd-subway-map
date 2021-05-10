package wooteco.subway.domain.line.value;

import java.util.Objects;

public final class LineId extends Number {

    private final Long id;

    public LineId(Long id) {
        this.id = id;
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
