package wooteco.subway.domain;

public class Section {
    private final Long id;
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section create(Long lineId, Station upStation, Station downStation, int distance) {
        return new Section(lineId, upStation, downStation, distance);
    }

    public boolean isEqualsAndSmallerThan(int distance) {
        return this.distance <= distance;
    }

    public boolean isSameUpDownStation(Long upStationId, Long downStationId) {
        return this.upStation.getId().equals(upStationId) &&
                this.downStation.getId().equals(downStationId);
    }

    public boolean haveStationId(Long upStationId, Long downStationId) {
        return this.upStation.getId().equals(downStationId) || this.downStation.getId().equals(upStationId);
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

    public int getDistance() {
        return distance;
    }

}
