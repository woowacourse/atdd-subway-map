package wooteco.subway.domain.section;

import java.util.stream.Stream;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exceptions.StationNotFoundException;

public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

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

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Station getStationById(Long id) {
        return Stream.of(upStation, downStation)
            .filter(station -> id.equals(station.getId()))
            .findFirst()
            .orElseThrow(StationNotFoundException::new);
    }
}