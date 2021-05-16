package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;

public class Section {
    private Long id;
    private Station upwardStation;
    private Station downwardStation;
    private int distance;

    public Section(Station upwardStation, Station downwardStation, int distance) {
        this.upwardStation = upwardStation;
        this.downwardStation = downwardStation;
        this.distance = distance;
    }

    public Section(Long id, Station upwardStation, Station downwardStation, int distance) {
        this.id = id;
        this.upwardStation = upwardStation;
        this.downwardStation = downwardStation;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpwardStation() {
        return this.upwardStation;
    }

    public Station getDownwardStation() {
        return this.downwardStation;
    }

    public Long getUpStationId() {
        return this.upwardStation.getId();
    }

    public Long getDownStationId() {
        return this.downwardStation.getId();
    }

    public boolean hasUpwardStation(Long stationId) {
        return this.upwardStation.hasSameId(stationId);
    }

    public boolean hasDownwardStation(Long stationId) {
        return this.downwardStation.hasSameId(stationId);
    }

    public boolean hasLongerDistanceThan(Section existingSection) {
        return this.distance >= existingSection.distance;
    }
}
