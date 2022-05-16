package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long id, Station upStation, Station downStation, int distance) {
        validateMoreThanZero(distance);
        validateDifferentStation(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public boolean contains(final Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean containsSameStations(final Section other) {
        return this.contains(other.getUpStation()) && this.contains(other.getDownStation());
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    private void validateMoreThanZero(final int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리가 1 미만인 구간 정보는 생성할 수 없습니다.");
        }
    }

    private void validateDifferentStation(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 같은 역일 수 없습니다.");
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
        return Objects.equals(id, section.id) && distance == section.distance && upStation.equals(section.upStation)
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
