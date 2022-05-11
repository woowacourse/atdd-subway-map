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

    public boolean matchUpStationAndDownStation(Station upStation, Station downStation) {
        return upStation.equals(this.upStation) && downStation.equals(this.downStation);
    }

    public boolean matchUpStation(Station station) {
        return station.equals(this.upStation);
    }

    public boolean hasStation(Station station) {
        return station.equals(this.upStation) || station.equals(this.downStation);
    }

    public void checkDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("구간 사이의 거리가 너무 멉니다.");
        }
    }

    public int subtractDistance(int distance) {
        return this.distance - distance;
    }

    public Section union(Section section, Station station) {
        if (this.upStation.equals(station)) {
            return new Section(null, this.lineId, section.upStation, this.downStation,
                    section.distance + this.distance);
        }
        return new Section(null, this.lineId, this.upStation, section.downStation, section.distance + this.distance);
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
