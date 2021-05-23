package wooteco.subway.domain.section;

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

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public long getUpStationId() {
        return upStation.getId();
    }

    public Station getDownStation() {
        return downStation;
    }

    public long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    public void setId(long sectionId) {
        this.id = sectionId;
    }
}
