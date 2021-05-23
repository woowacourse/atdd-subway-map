package wooteco.subway.domain;

import wooteco.subway.domain.station.Station;

public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
}
