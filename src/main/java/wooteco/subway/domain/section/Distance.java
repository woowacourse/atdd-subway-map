package wooteco.subway.domain.section;

import java.util.Objects;

public class Distance {

    private final int distance;

    public Distance(int distance) {
        validateDistancePositive(distance);
        this.distance = distance;
    }

    private void validateDistancePositive(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 양수여야 합니다.");
        }
    }

    public boolean isCloserThan(Distance other) {
        return this.distance < other.distance;
    }

    public int calculateDifferenceBetween(Distance other) {
        return Math.abs(this.distance - other.distance);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
