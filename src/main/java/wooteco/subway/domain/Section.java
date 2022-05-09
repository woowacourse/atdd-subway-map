package wooteco.subway.domain;

import wooteco.subway.dto.LineRequest;

public class Section {
    private Long id;
    private Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    private Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section from(Long lineId, LineRequest lineRequest) {
        return new Section(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
            lineRequest.getDistance());
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
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

        Section section = (Section)o;

        if (getDistance() != section.getDistance())
            return false;
        if (getLineId() != null ? !getLineId().equals(section.getLineId()) : section.getLineId() != null)
            return false;
        if (getUpStationId() != null ? !getUpStationId().equals(section.getUpStationId()) :
            section.getUpStationId() != null)
            return false;
        return getDownStationId() != null ? getDownStationId().equals(section.getDownStationId()) :
            section.getDownStationId() == null;
    }

    @Override
    public int hashCode() {
        int result = getLineId() != null ? getLineId().hashCode() : 0;
        result = 31 * result + (getUpStationId() != null ? getUpStationId().hashCode() : 0);
        result = 31 * result + (getDownStationId() != null ? getDownStationId().hashCode() : 0);
        result = 31 * result + getDistance();
        return result;
    }
}
