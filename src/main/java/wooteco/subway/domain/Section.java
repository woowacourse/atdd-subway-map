package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(long id, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean contains(final Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    private void validateDistance(final int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리가 1 미만인 구간 정보는 생성할 수 없습니다.");
        }
    }

    public long getId() {
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

    public void changeUpStation(final Station station) {
        this.upStation = station;
    }

    public void changeDownStation(final Station station) {
        this.downStation = station;
    }

    public void changeDistance(final int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return id == section.id && distance == section.distance && upStation.equals(section.upStation)
                && downStation.equals(section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                ", distance=" + distance +
                '}';
    }
}
