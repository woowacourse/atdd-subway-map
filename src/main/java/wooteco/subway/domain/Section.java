package wooteco.subway.domain;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Section section) {
        this(id, section.upStation, section.downStation, section.distance);
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = Objects.requireNonNull(upStation);
        this.downStation = Objects.requireNonNull(downStation);
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

    public boolean isUpStationEquals(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStationEquals(Station station) {
        return downStation.equals(station);
    }

    public Section updateUpStation(Station station) {
        return new Section(station, downStation, distance);
    }

    public Section updateDownStation(Station station) {
        return new Section(upStation, station, distance);
    }

    public Section updateDistance(int distance) {
        return new Section(upStation, downStation, distance);
    }
}
