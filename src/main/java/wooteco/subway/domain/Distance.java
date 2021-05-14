package wooteco.subway.domain;

public class Distance {
    private final int distance;

    public Distance(int distance) {
        this.distance = distance;
    }

    public int calculateMax(Distance comparedDistance) {
        return Math.max(distance, comparedDistance.distance);
    }

    public int calculateMin(Distance comparedDistance) {
        return Math.min(distance, comparedDistance.distance);
    }

    public boolean isLongerDistanceThan(SimpleSection section) {
        return distance > section.getDistance();
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return distance;
    }
}
