package wooteco.subway.domain;

import java.util.Objects;

public class SectionEntity {

    private final Long sectionId;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionEntity(Long sectionId, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.sectionId = sectionId;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getSectionId() {
        return sectionId;
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SectionEntity that = (SectionEntity)o;
        return distance == that.distance && Objects.equals(sectionId, that.sectionId) && Objects.equals(
            lineId, that.lineId) && Objects.equals(upStationId, that.upStationId) && Objects.equals(
            downStationId, that.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId, lineId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionEntity{" +
            "sectionId=" + sectionId +
            ", lineId=" + lineId +
            ", upStationId=" + upStationId +
            ", downStationId=" + downStationId +
            ", distance=" + distance +
            '}';
    }
}
