package wooteco.subway.section.domain;

import wooteco.subway.common.exception.bad_request.WrongSectionInfoException;

public class Distance {
    private final int distance;

    public Distance(int distance) {
        validatePositive(distance);
        this.distance = distance;
    }

    private void validatePositive(int distance) {
        if (distance <= 0) {
            throw new WrongSectionInfoException(String.format("거리는 양수여야 합니다. 입력 값 : %d", distance));
        }
    }

    public int distance() {
        return distance;
    }

    public boolean isLessDistance(Distance newDistance) {
        return this.distance > newDistance.distance;
    }

    public Distance subtract(Distance newDistance) {
        return new Distance(distance - newDistance.distance);
    }

    public Distance sum(Distance newDistance) {
        return new Distance(distance + newDistance.distance);
    }
}