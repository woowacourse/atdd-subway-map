package wooteco.subway.section.model;

import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.WrongDistanceException;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.model.Station;

import java.util.List;

public class Section {

    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation, distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }

    private void validateSection(Station upStation, Station downStation, int distance) throws WrongDistanceException {
        validateSameStations(upStation, downStation);
        validateDistance(distance);
    }

    private void validateDistance(int distance) throws WrongDistanceException {
        if (distance <= 0) {
            throw new WrongDistanceException("");
        }
    }

    private void validateSameStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new DuplicationException("상행역과 하행역은 동일할 수 없습니다.");
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }
}
