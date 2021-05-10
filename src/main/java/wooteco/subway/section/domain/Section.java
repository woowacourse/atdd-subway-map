package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(Station upStation, Station downStation, Distance distance) {
        this(null, null, upStation, downStation, distance);
    }

    public Section(Long lineId, Station upStation, Station downStation, Distance distance) {
        this(null, lineId, upStation, downStation, distance);
    }
    public Section(Long id, Long lineId, Station upStation, Station downStation, Distance distance) {
        validateStations(upStation, downStation);
        validateNotEmpty(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateNotEmpty(Distance distance) {
        if (distance == null) {
            throw new IllegalArgumentException("거리를 입력해주세요.");
        }
    }
    private void validateStations(Station upStation, Station downStation) {
        if (upStation == null || downStation == null) {
            throw new IllegalArgumentException("역을 모두 입력해주세요.");
        }
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

    public Distance getDistance() {
        return distance;
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean hasSameUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean hasSameDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isLessDistance(Section newSection) {
        return distance.isLessDistance(newSection.distance);
    }

    public Section changeSection(Section newSection) {
        if (upStation.equals(newSection.upStation)) {
            return new Section(id, lineId, newSection.downStation, downStation,
                    distance.calculate(newSection.distance));
        }
        if (downStation.equals(newSection.downStation)) {
            return new Section(id, lineId, upStation, newSection.upStation,
                    distance.calculate(newSection.distance));
        }
        throw new IllegalArgumentException("구간이 변경될 수 없습니다.");
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