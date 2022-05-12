package wooteco.subway.domain;

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
}
