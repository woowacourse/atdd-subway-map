package wooteco.subway.section;

import wooteco.subway.station.Station;

public class Section {

    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Distance distance() {
        return distance;
    }
}
