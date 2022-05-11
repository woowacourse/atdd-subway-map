package wooteco.subway.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Section {
    private final Long id;
    private final Long upStationId;
    private final Long downStationId;
    private final Distance distance;

    public Section(Long id, Long upStationId, Long downStationId, Distance distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Distance distance) {
        this(null, upStationId, downStationId, distance);
    }

    public Section(Long id, Section section) {
        this(id, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public boolean isSameUpStation(Section other) {
        return upStationId.equals(other.upStationId);
    }

    public boolean isSameDownStation(Section other) {
        return downStationId.equals(other.downStationId);
    }

    public boolean isSameEitherUpOrDownStation(Section other) {
        return isSameUpStation(other) || isSameDownStation(other);
    }

    public boolean isDistanceLessThanOrEqualTo(Section other) {
        return distance.isLessThanOrEqualTo(other.distance);
    }

    public boolean hasSameStation(Section other) {
        HashSet<Long> stationIds = new HashSet<>(
                List.of(upStationId, downStationId, other.upStationId, other.downStationId));
        return stationIds.size() < 4;
    }

    public Distance addDistance(Section other) {
        return distance.add(other.distance);
    }

    public Distance subtractDistance(Section other) {
        return distance.subtract(other.distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance,
                section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
