package wooteco.subway.domain;

public class Section {

    private final long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
