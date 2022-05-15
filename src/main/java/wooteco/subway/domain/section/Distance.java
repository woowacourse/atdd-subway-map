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

    public boolean isLongerThan(Distance other) {
        return this.distance > other.distance;
    }

    public int calculateDifferenceBetween(Distance other) {
        return Math.abs(this.distance - other.distance);
    }

    public int calculateSumBetween(Distance other) {
        return this.distance + other.distance;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Distance{" + distance + '}';
    }
}
