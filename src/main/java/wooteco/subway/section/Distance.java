package wooteco.subway.section;

import wooteco.subway.line.LineException;

public class Distance {
    private final int value;

    public Distance(final int value) {
        if (value <= 0) {
            throw new LineException("올바르지 않은 Distance 값입니다. + " + value);
        }
        this.value = value;
    }

    public int value() {
        return value;
    }

    public Distance add(final Distance distance) {
        return new Distance(this.value + distance.value);
    }

    public Distance add(final int distance) {
        return add(new Distance(distance));
    }

    public Distance sub(final Distance distance) {
        return new Distance(this.value - distance.value);
    }

    public Distance sub(final int distance) {
        return sub(new Distance(distance));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value == distance.value;
    }
}
