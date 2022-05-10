package wooteco.subway.domain;

public class Section {

    private final Long id;
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
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
