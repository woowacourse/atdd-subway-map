package wooteco.subway.domain;

import java.util.Objects;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0보다 작을 수 없습니다.");
        }
    }

    public boolean hasSameStations(Section section) {
        return (downStation.equals(section.getDownStation()) && upStation.equals(section.getUpStation())) ||
            (downStation.equals(section.getUpStation()) && upStation.equals(section.getDownStation()));
    }

    public boolean hasSameUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean hasSameDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public boolean contains(Station station) {
        return upStation.equals(station) || downStation.equals(station);
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(
            upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }
}
