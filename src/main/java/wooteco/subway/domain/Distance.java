package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.InvalidDistanceException;

public class Distance {

    private static final int MINIMUM_DISTANCE = 1;

    private final int value;

    public Distance(int value) {
        this.value = value;
        validatePositive(this.value);
    }

    private void validatePositive(int value) {
        if (value < MINIMUM_DISTANCE) {
            throw new InvalidDistanceException(MINIMUM_DISTANCE);
        }
    }

    public int getValue() {
        return value;
    }

    public Distance add(Distance distance) {
        return new Distance(value + distance.getValue());
    }

    public Distance subtract(Distance distance) {
        return new Distance(value - distance.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
