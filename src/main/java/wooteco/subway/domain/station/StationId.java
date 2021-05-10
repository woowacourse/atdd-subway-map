package wooteco.subway.domain.station;

import java.util.Objects;

public class StationId extends Number {

    private final Long id;

    public StationId(Long id) {
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
        return id.floatValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationId stationId = (StationId) o;
        return Objects.equals(id, stationId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
