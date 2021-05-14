package wooteco.subway.domain;

import java.util.Objects;

public class SimpleSection {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SimpleSection() {
    }

    public SimpleSection(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isSameBetweenUpAndDownStation() {
        return upStationId.equals(downStationId);
    }


    public boolean isDistanceMoreThanZero() {
        return distance > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSection that = (SimpleSection) o;
        if (distance != that.distance) return false;
        if (!Objects.equals(upStationId, that.upStationId)) return false;
        return Objects.equals(downStationId, that.downStationId);
    }

    @Override
    public int hashCode() {
        int result = upStationId != null ? upStationId.hashCode() : 0;
        result = 31 * result + (downStationId != null ? downStationId.hashCode() : 0);
        result = 31 * result + distance;
        return result;
    }
}
