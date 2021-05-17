package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

public class Section {
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(new Line(lineId),
                new Station(upStationId),
                new Station(downStationId),
                distance);
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

    public boolean isLessOrSameDistance(int distance) {
        return this.distance <= distance;
    }
}
