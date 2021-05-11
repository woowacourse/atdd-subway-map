package wooteco.subway.domain.line.value.section;

import wooteco.subway.exception.line.NegativeOrZeroDistanceException;

import java.util.Objects;

public class Distance extends Number {

    private final Long distance;

    public Distance(Long distance) {
        validateThatIsNegativeOrZeroNumber(distance);
        this.distance = distance;
    }

    private void validateThatIsNegativeOrZeroNumber(Long id) {
        if(id <= 0) {
            throw new NegativeOrZeroDistanceException();
        }
    }

    @Override
    public int intValue() {
        return distance.intValue();
    }

    @Override
    public long longValue() {
        return distance.longValue();
    }

    @Override
    public float floatValue() {
        return distance.floatValue();
    }

    @Override
    public double doubleValue() {
        return distance.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return Objects.equals(distance, distance1.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

}
