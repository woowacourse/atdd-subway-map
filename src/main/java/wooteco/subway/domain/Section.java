package wooteco.subway.domain;

public class Section {
    private final Long id;
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
        this(null, upStation, downStation, distance);
    }

    public void splitRightBy(Section section) {
        splitBy(section, true);
    }

    public void splitLeftBy(Section section) {
        splitBy(section, false);
    }

    public void splitBy(Section section, boolean direction) {
        checkStations(section);
        checkDistance(section);
        if (direction) {
            upStation = section.downStation;
        }
        if (!direction) {
            downStation = section.upStation;
        }
        distance = distance - section.distance;
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

    public boolean hasSameUpStationWith(Section section) {
        return isUpStation(section.upStation);
    }

    public boolean hasSameDownStationWith(Section section) {
        return isDownStation(section.downStation);
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
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
