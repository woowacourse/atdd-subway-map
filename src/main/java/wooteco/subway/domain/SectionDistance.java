package wooteco.subway.domain;

import wooteco.subway.exception.section.DistancePreviousOverException;

import java.util.Objects;

public class SectionDistance {

    private final int distance;

    public SectionDistance(int distance) {
        validateIsPositive(distance);
        this.distance = distance;
    }

    private void validateIsPositive(int distance) {
        if (distance <= 0) {
            throw new DistancePreviousOverException();
        }
    }

    public SectionDistance subtract(SectionDistance target) {
        return new SectionDistance(distance - target.distance);
    }

    public int intValue() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionDistance that = (SectionDistance) o;
        return distance == that.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }
}
