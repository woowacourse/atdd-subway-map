package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateHasSameStation(upStationId, downStationId);
        validatePositiveDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validateHasSameStation(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("상행역, 하행역은 다른 역이어야 합니다.");
        }
    }

    private void validatePositiveDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 사이의 거리는 0보다 커야합니다.");
        }
    }

    public boolean isNewLastStation(long currentLastUpStationId, long currentLastDownStationId) {
        return upStationId == currentLastDownStationId || downStationId == currentLastUpStationId;
    }

    public boolean isUpStation(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean isDownStation(Long stationId) {
        return downStationId.equals(stationId);
    }

    public boolean hasStation(Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return getDistance() == section.getDistance() &&
                Objects.equals(getId(), section.getId()) &&
                Objects.equals(getLineId(), section.getLineId()) &&
                Objects.equals(getUpStationId(), section.getUpStationId()) &&
                Objects.equals(getDownStationId(), section.getDownStationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLineId(), getUpStationId(), getDownStationId(), getDistance());
    }
}
