package wooteco.subway.domain;

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

    public boolean isDividable(Section other) {
        return isOneStationMatch(other) && this.distance.isLongerThan(other.distance);
    }

    private boolean isOneStationMatch(Section other) {
        return this.upStation.equals(other.upStation) ^ this.downStation.equals(other.downStation);
    }

    public Section divide(Section other) {
        if (isUpStationConnected(other)) {
            return new Section(other.downStation, downStation, distance.subtract(other.distance));
        }
        return new Section(upStation, other.upStation, distance.subtract(other.distance));
    }

    private boolean isUpStationConnected(Section other) {
        return this.upStation.equals(other.upStation);
    }

    public boolean isAnyIdMatch(Long stationId) {
        return upStation.getId().equals(stationId) || downStation.getId().equals(stationId);
    }

    public Section merge(Section other) {
        return new Section(id, upStation, other.downStation, distance.plus(other.distance));
    }

    public Long getId() {
        return id;
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
