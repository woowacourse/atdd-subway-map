package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.HashMap;
import java.util.Map;

public class Section {
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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
