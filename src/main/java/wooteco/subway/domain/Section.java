package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(final Long upStationId, final Long downStationId, final int distance) {
        validateDistance(distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean existStation(final Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 거리는 1 이상이어야 합니다.");
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(upStationId, section.upStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId);
    }
}
