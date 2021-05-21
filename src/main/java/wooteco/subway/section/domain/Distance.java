package wooteco.subway.section.domain;

import java.util.Objects;
import wooteco.subway.exception.section.InvalidDistanceException;

public class Distance {

    private static final int MINIMUM_DISTANCE = 1;

    private final int value;

    public Distance(final int value) {
        validatePositive(value);
        this.value = value;
    }

    private void validatePositive(final int value) {
        if (value < MINIMUM_DISTANCE) {
            throw new InvalidDistanceException(MINIMUM_DISTANCE);
        }
    }

    public Distance add(final Distance distance) {
        return new Distance(value + distance.value());
    }

    public Distance subtract(final Distance distance) {
        return new Distance(value - distance.value());
    }

    public int value() {
        return value;
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