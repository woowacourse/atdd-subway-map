package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public boolean isConnectedSection(final Section otherSection) {
        return contain(otherSection.upStation) || contain(otherSection.downStation);
    }

    private boolean contain(final Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }
}
