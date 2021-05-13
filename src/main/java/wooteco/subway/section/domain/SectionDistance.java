package wooteco.subway.section.domain;

import wooteco.subway.section.exception.SectionDistanceTooShortException;

import java.util.Objects;

public class SectionDistance {
    private static final int MINIMUM_DISTANCE = 0;
    private final long distance;

    public SectionDistance(long distance) {
        whenDistanceIsNegative(distance);
        this.distance = distance;
    }

    private void whenDistanceIsNegative(long distance) {
        if (distance <= MINIMUM_DISTANCE) {
            throw new SectionDistanceTooShortException(String.format("구간 거리는 %d보다 커야합니다. 입력된 거리 : %d", MINIMUM_DISTANCE, distance));
        }
    }

    public long getDistance() {
        return distance;
    }

    public SectionDistance sum(SectionDistance that) {
        return new SectionDistance(this.distance + that.getDistance());
    }

    public SectionDistance minus(SectionDistance that) {
        return new SectionDistance(this.distance - that.getDistance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionDistance)) return false;
        SectionDistance that = (SectionDistance) o;
        return getDistance() == that.getDistance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDistance());
    }

    @Override
    public String toString() {
        return String.valueOf(distance);
    }
}
