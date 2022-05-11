package wooteco.subway.domain;

import java.util.Objects;

public class Adjacency {
    private final StationInfo left;
    private final StationInfo right;

    public Adjacency(StationInfo left, StationInfo right) {
        this.left = left;
        this.right = right;
    }

    public boolean isRightBlank() {
        return this.right.isBlankLink();
    }

    public boolean isLeftBlank() {
        return this.left.isBlankLink();
    }

    public StationInfo copyLeft() {
        return new StationInfo(left.getLinkedStationId(), left.getDistance());
    }

    public StationInfo copyRight() {
        return new StationInfo(right.getLinkedStationId(), right.getDistance());
    }

    public StationInfo getLeft() {
        return left;
    }

    public StationInfo getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Adjacency adjacency = (Adjacency) o;
        return Objects.equals(left, adjacency.left) && Objects.equals(right, adjacency.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
