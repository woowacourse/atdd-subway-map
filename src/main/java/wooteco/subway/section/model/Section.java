package wooteco.subway.section.model;

import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.WrongDistanceException;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.model.Station;

public class Section {

    private final Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation, distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(Station upStation, Station downStation, int distance) throws WrongDistanceException {
        validateSameStations(upStation, downStation);
        validateDistance(distance);
    }

    private void validateSameStations(Station upStationId, Station downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new DuplicationException("상행역과 하행역은 동일할 수 없습니다.");
        }
    }

    private void validateDistance(int distance) throws WrongDistanceException {
        if (distance <= 0) {
            throw new WrongDistanceException("");
        }
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
