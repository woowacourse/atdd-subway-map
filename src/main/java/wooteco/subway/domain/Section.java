package wooteco.subway.domain;

import wooteco.subway.exception.StationDuplicateException;

public class Section {

    private static final int MIN_DISTANCE = 1;

    private final Long id;
    private final long lineId;

    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(final long lineId, final Station upStation, final Station downStation, final int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(final Long id, final Section section) {
        this(id, section.getLineId(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public Section(final Long id, final long lineId, final Station upStation, final Station downStation,
            final int distance) {
        validateDistance(distance);
        validateDuplicateStation(upStation, downStation);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(final int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException("두 종점간의 거리는 0보다 커야합니다.");
        }
    }

    private void validateDuplicateStation(final Station upStation, final Station downStation) {
        if (upStation.isSameStation(downStation)) {
            throw new StationDuplicateException();
        }
    }

    public void updateSection(final Station upStation, final Station downStation, final int overlappingDistance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = decreaseDistance(overlappingDistance);
    }

    private int decreaseDistance(final int overlappingDistance) {
        return this.distance -= overlappingDistance;
    }

    public boolean isDuplicateSection(final Section section) {
        return (isSameUpStation(section.getUpStation()) && isSameDownStation(section.getDownStation())) ||
                (isSameUpStation(section.getDownStation()) && isSameDownStation(section.getUpStation()));
    }

    public boolean hasSectionToConnect(final Section section) {
        if (isSameUpStation(section.getUpStation()) || isSameDownStation(section.getDownStation())) {
            return isLongerThan(section.getDistance());
        }
        return isSameUpStation(section.getDownStation()) || isSameDownStation(section.getUpStation());
    }

    private boolean isLongerThan(final int distance) {
        return this.distance > distance;
    }

    private boolean isSameUpStation(final Station station) {
        return upStation.isSameStation(station);
    }

    private boolean isSameDownStation(final Station station) {
        return downStation.isSameStation(station);
    }

    public Long getId() {
        return id;
    }

    public long getLineId() {
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
