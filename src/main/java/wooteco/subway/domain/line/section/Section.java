package wooteco.subway.domain.line.section;

import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.line.value.section.SectionId;
import wooteco.subway.domain.station.value.StationId;

import java.util.Objects;

public class Section {
    private final SectionId id;
    private final LineId lineId;
    private final StationId upStationId;
    private final StationId downStationId;
    private final Distance distance;

    public Section(SectionId id, LineId lineId, StationId upStationId, StationId downStationId, Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(LineId lineId, StationId upStationId, StationId downStationId, Distance distance) {
        this(SectionId.empty(), lineId, upStationId, downStationId, distance);
    }

    public Section(StationId upStationId, StationId downStationId, Distance distance) {
        this(SectionId.empty(), LineId.empty(), upStationId, downStationId, distance);
    }

    public boolean hasSameUpStationId(Section section) {
        return upStationId.equals(section.upStationId);
    }

    public boolean hasSameUpStationId(Long stationId) {
        return upStationId.longValue() == stationId;
    }

    public boolean hasSameDownStationId(Section section) {
        return downStationId.equals(section.downStationId);
    }

    public boolean hasSameDownStationId(Long stationId) {
        return downStationId.longValue() == stationId;
    }

    public boolean hasSameId(Section changedSection) {
        return id.equals(changedSection.id);
    }

    public boolean hasSameId(SectionId sectionId) {
        return id.equals(sectionId);
    }

    public Long getId() {
        return id.longValue();
    }

    public Long getLineId() {
        return lineId.longValue();
    }

    public Long getUpStationId() {
        return upStationId.longValue();
    }

    public Long getDownStationId() {
        return downStationId.longValue();
    }

    public Long getDistance() {
        return distance.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) &&
                Objects.equals(upStationId, section.upStationId) &&
                Objects.equals(downStationId, section.downStationId) &&
                Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
