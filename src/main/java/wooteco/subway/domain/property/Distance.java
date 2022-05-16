package wooteco.subway.domain.property;

import java.util.Objects;

import wooteco.subway.exception.InvalidRequestException;

public class Distance {

    private final int value;

    public Distance(int value) {
        validatePositive(value);
        this.value = value;
    }

    private void validatePositive(int value) {
        if (value <= 0) {
            throw new InvalidRequestException("구간의 거리는 0 이하가 될 수 없습니다.");
        }
    }

    public Distance plus(Distance other) {
        return new Distance(this.value + other.value);
    }

    public Distance subtract(Distance other) {
        return new Distance(this.value - other.value);
    }

    public boolean isLongerThan(Distance other) {
        return this.value > other.value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Distance distance = (Distance)o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
