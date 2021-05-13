package wooteco.subway.domain;

import java.util.Objects;

public class Distance {

    private static final int VALUE_MIN = 0;

    private final int value;

    public Distance(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (VALUE_MIN >= value) {
            throw new IllegalArgumentException("거리 값은 자연수 입니다.");
        }
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
