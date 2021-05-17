package wooteco.subway.section;

import wooteco.subway.section.exception.SectionDistanceException;
import wooteco.subway.section.exception.SectionException;
import wooteco.subway.station.Station;

import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        validateLineField(upStation, downStation, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateLineField(Station upStation, Station downStation, int distance) {
        validateStationExistence(upStation, downStation);
        validateDistance(distance);
    }

    private void validateStationExistence(Station upStation, Station downStation) {
        if (upStation == null || downStation == null) {
            throw new SectionException("구간에는 양 역이 존재해야 합니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new SectionException("구간의 거리는 0 초과여야 합니다.");
        }
    }

    public void validateSectionDistance(SectionDto sectionDto) {
        if (sectionDto.getDistance() >= this.getDistance()) {
            throw new SectionDistanceException();
        }
    }

    public boolean isSameUpStation(Long upStationId) {
        return upStation.getId().equals(upStationId);
    }

    public boolean isSameDownStation(Long downStationId) {
        return downStation.getId().equals(downStationId);
    }

    public boolean includeThisLine(Long id) {
        return this.lineId.equals(id);
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

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
