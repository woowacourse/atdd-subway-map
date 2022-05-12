package wooteco.subway.domain;

public class Section {
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
        this(null, upStation, downStation, distance);
    }

    Section splitRightBy(Section section) {
        return splitBy(section, true);
    }

    Section splitLeftBy(Section section) {
        return splitBy(section, false);
    }

    Section splitBy(Section section, boolean direction) {
        checkStations(section);
        checkDistance(section);
        int splitDistance = distance - section.distance;
        if (direction) {
            return new Section(id, section.downStation, downStation, splitDistance);
        }
        return new Section(id, upStation, section.upStation, splitDistance);
    }

    private void checkStations(Section section) {
        if (hasSameUpStationWith(section) && hasSameDownStationWith(section)) {
            throw new IllegalArgumentException("기존 구간과 양 방향 종점이 같아 추가할 수 없습니다.");
        }
    }

    private void checkDistance(Section section) {
        if (section.distance >= distance) {
            throw new IllegalArgumentException("기존 구간보다 거리가 길어 추가할 수 없습니다.");
        }
    }

    public Section mergeWith(Section section) {
        int mergedDistance = this.distance + section.distance;
        return new Section(id, upStation, section.downStation, mergedDistance);
    }

    boolean hasSameUpStationWith(Section section) {
        return isUpStation(section.upStation);
    }

    boolean hasSameDownStationWith(Section section) {
        return isDownStation(section.downStation);
    }

    boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
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
