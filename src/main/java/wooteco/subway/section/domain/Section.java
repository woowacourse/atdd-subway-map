package wooteco.subway.section.domain;

import wooteco.subway.exception.illegalexception.IllegalSectionArgumentException;
import wooteco.subway.station.domain.Station;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(Station upStationId, Station downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalSectionArgumentException();
        }
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(0L, lineId, upStation, downStation, distance);
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
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
        if (!getUpStation().equals(section.getUpStation())) return false;
        return getDownStation().equals(section.getDownStation());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getLineId().hashCode();
        result = 31 * result + getUpStation().hashCode();
        result = 31 * result + getDownStation().hashCode();
        result = 31 * result + getDistance();
        return result;
    }
}
