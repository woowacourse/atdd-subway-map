package wooteco.subway.section;

import wooteco.subway.station.Station;

public class Section {

    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(Station upStation, Station downStation, Distance distance) {
        validate(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 같으면 안됩니다.");
        }
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
