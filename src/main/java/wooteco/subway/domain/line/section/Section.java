package wooteco.subway.domain.line.section;

import java.util.Objects;

public class Section {
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

    public Section(Long lineId, Long upStationId, Long downStationId, Long distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Long distance) {
        this(null, upStationId, downStationId, distance);
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

    public Long getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }

}
