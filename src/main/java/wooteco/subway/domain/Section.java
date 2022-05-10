package wooteco.subway.domain;

import wooteco.subway.exception.IllegalDistanceException;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id,
                   final Station upStation,
                   final Station downStation,
                   final int distance) {
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalDistanceException();
        }
    }

    public Section(final Station upStation,
                   final Station downStation,
                   final int distance) {
        this(null, upStation, downStation, distance);
    }

    public boolean isUpStationMatch(final Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStationMatch(final Station station) {
        return downStation.equals(station);
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
