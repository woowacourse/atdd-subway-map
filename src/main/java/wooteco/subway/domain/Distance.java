package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.MinimumSectionDistanceException;

public class Distance {

    private static final int MIN_VALUE = 1;

    private final int value;

    public Distance(final int value) {
        validateDistance(value);
        this.value = value;
    }

    private void validateDistance(final int value) {
        if (value < MIN_VALUE) {
            throw new MinimumSectionDistanceException();
        }
    }



    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance)) {
            return false;
        }
        final Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
