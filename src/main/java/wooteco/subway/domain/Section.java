package wooteco.subway.domain;

import wooteco.subway.exception.DuplicateException;

public class Section {

    private final Id id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(Long key, Section section) {
        this(new Id(key), section.line, section.upStation, section.downStation, section.distance);
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Id id, Line line, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        validateDuplicateStations(this.upStation, this.downStation);
    }

    private void validateDuplicateStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new DuplicateException();
        }
    }

    public Section updateForSave(Section section) {
        Distance updateDistance = this.distance.subtract(section.distance);

        if (upStation.equals(section.upStation)) {
            return new Section(null, line, section.downStation, downStation, updateDistance);
        }
        return new Section(null, line, upStation, section.upStation, updateDistance);
    }

    public Section updateForDelete(Section section) {
        Distance updateDistance = section.distance.add(this.distance);
        return new Section(null, line, upStation, section.downStation, updateDistance);
    }

    public boolean hasSameStationBySection(Section section) {
        return hasSameStation(section.getUpStation()) ||
            hasSameStation(section.getDownStation());
    }

    public boolean hasSameStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean isMatchUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isMatchDownStation(Station station) {
        return downStation.equals(station);
    }

    public Long getId() {
        return id.getValue();
    }

    public Line getLine() {
        return line;
    }

    public Long getLineId() {
        return line.getId();
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

    public Distance getDistance() {
        return distance;
    }

    public int getDistanceValue() {
        return distance.getValue();
    }
}
