package wooteco.subway.section.domain;

import wooteco.subway.exception.IllegalSectionArgumentException;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateSection(upStationId, downStationId);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(0L, lineId, upStationId, downStationId, distance);
    }

    public boolean isUpStationId(Long id) {
        return this.upStationId.equals(id);
    }

    public boolean isDownStationId(Long id) {
        return this.downStationId.equals(id);
    }

    public boolean compareDistance(int distance) {
        return this.distance <= distance;
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
        if (!(o instanceof Section)) return false;

        Section section = (Section) o;

        if (getDistance() != section.getDistance()) return false;
        if (!getId().equals(section.getId())) return false;
        if (!getLineId().equals(section.getLineId())) return false;
        if (!getUpStationId().equals(section.getUpStationId())) return false;
        return getDownStationId().equals(section.getDownStationId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getLineId().hashCode();
        result = 31 * result + getUpStationId().hashCode();
        result = 31 * result + getDownStationId().hashCode();
        result = 31 * result + getDistance();
        return result;
    }

    private void validateSection(Long upStationId, Long downStationId) {
        if(upStationId.equals(downStationId)) {
            throw new IllegalSectionArgumentException();
        }
    }
}
