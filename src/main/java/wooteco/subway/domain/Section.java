package wooteco.subway.domain;

import java.util.HashSet;
import java.util.List;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Distance distance;

    private Section(Long id, Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long upStationId, Long downStationId, Distance distance) {
        this(null, upStationId, downStationId, distance);
    }

    public Section(Long id, Section section) {
        this(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Section(Section section, Distance distance) {
        this(section.id, section.lineId, section.upStationId, section.getDownStationId(), distance);
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

    public boolean isDistanceLessThan(Section other) {
        return distance.isLessThan(other.distance);
    }

    public boolean hasSameStation(Section other) {
        HashSet<Long> stationIds = new HashSet<>(List.of(upStationId, downStationId, other.upStationId, other.downStationId));
        return stationIds.size() < 4;
    }

    public Distance subtractDistance(Section other) {
        return distance.subtract(other.distance);
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

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (id != null ? !id.equals(section.id) : section.id != null) return false;
        if (lineId != null ? !lineId.equals(section.lineId) : section.lineId != null) return false;
        if (upStationId != null ? !upStationId.equals(section.upStationId) : section.upStationId != null) return false;
        if (downStationId != null ? !downStationId.equals(section.downStationId) : section.downStationId != null)
            return false;
        return distance != null ? distance.equals(section.distance) : section.distance == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (lineId != null ? lineId.hashCode() : 0);
        result = 31 * result + (upStationId != null ? upStationId.hashCode() : 0);
        result = 31 * result + (downStationId != null ? downStationId.hashCode() : 0);
        result = 31 * result + (distance != null ? distance.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
