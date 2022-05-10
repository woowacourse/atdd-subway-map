package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private Long distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long lineId, Station upStation, Station downStation, Long distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }
}
