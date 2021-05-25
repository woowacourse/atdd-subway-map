package wooteco.subway.line.domain.section;

import java.util.Objects;

public class Distance {

    private final int value;

    public Distance(final int value) {
        validateDistance(value);
        this.value = value;
    }

    private void validateDistance(final int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("거리는 0이상의 값을 가져야 합니다.");
        }
    }

    public boolean lessThanOrEqualTo(Distance distance) {
        return this.value <= distance.value;
    }

    public Distance minus(Distance distance) {
        return new Distance(this.value - distance.value);
    }

    public Distance plus(Distance distance) {
        return new Distance(this.value + distance.value);
    }

    public int value() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
