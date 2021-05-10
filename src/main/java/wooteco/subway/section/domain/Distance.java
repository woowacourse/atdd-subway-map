package wooteco.subway.section.domain;

public class Distance {
    private final int distance;

    public Distance(int distance) {
        validatePositive(distance);
        this.distance = distance;
    }

    private void validatePositive(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 양수여야 합니다.");
        }
    }

    public int distance() {
        return distance;
    }

    public boolean isLessDistance(Distance newDistance) {
        return this.distance > newDistance.distance;
    }

    public Distance calculate(Distance newDistance) {
        return new Distance(distance - newDistance.distance);
    }
}