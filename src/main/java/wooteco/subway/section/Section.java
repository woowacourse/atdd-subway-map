package wooteco.subway.section;

import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(Long id, Section section) {
        this(id, section.line, section.upStation, section.downStation, section.distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void updateDistance(int distance) {
        validatesDistanceCanAdd(distance);
        this.distance = this.distance - distance;
    }

    private void validatesDistanceCanAdd(int distance) {
        if (this.distance <= distance) {
            throw new SubwayException("넣을 수 없는 거리입니다.");
        }
    }

    public void updateUpStation(Station station) {
        this.upStation = station;
    }

    public void updateDownStation(Station station) {
        this.downStation = station;
    }

    public void update(Line line, Station upStation, Station downStation) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long lineId() {
        return line.getId();
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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
}
