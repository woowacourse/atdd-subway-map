package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;

public class Section {

    private static final Long TEMPORARY_ID = 0L;

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(TEMPORARY_ID, upStation, downStation, distance);
    }

    public boolean containsStation(Station station) {
        return station.equals(upStation) || station.equals(downStation);
    }

    public boolean equalsUpStation(Station station) {
        return equalsUpStation(station.getId());
    }

    public boolean equalsUpStation(Long stationId) {
        return stationId.equals(upStation.getId());
    }

    public boolean equalsDownStation(Station station) {
        return downStation.equals(station);
    }

    public boolean isCloserThan(Section section) {
        return this.distance < section.distance;
    }

    public int calculateDifferenceOfDistance(Section section) {
        return Math.abs(this.distance - section.distance);
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
}
