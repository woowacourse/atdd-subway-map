package wooteco.subway.domain.station.value;

import wooteco.subway.exception.line.NegativeIdException;

import java.util.Objects;

public class StationId extends Number {

    private final Long id;

    private StationId() {
        this.id = -1L;
    }

    public StationId(Long id) {
        validateThatIsNegativeNumber(id);
        this.id = id;
    }

    private void validateThatIsNegativeNumber(Long id) {
        if(id < 0) {
            throw new NegativeIdException();
        }
    }

    public static StationId empty() {
        return new StationId();
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
