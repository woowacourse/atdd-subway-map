package wooteco.subway.domain;

import java.util.Optional;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(Long id, Station upStation, Station downStation, Distance distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this(null, upStation, downStation, distance);
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
}
