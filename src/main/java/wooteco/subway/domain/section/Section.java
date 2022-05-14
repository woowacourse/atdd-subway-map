package wooteco.subway.domain.section;

import wooteco.subway.domain.Id;
import wooteco.subway.domain.station.Station;

public class Section {

    private final Id id;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;

    public Section(Id id, Station upStation, Station downStation, int distance) {
        validateStationsNotSame(upStation, downStation);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    private void validateStationsNotSame(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 동일할 수 없습니다.");
        }
    }

    public Section(Long id, Station upStation, Station downStation, int distance) {
        this(new Id(id), upStation, downStation, distance);
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(new Id(), upStation, downStation, distance);
    }

    public boolean containsStation(Station station) {
        return equalsUpStation(station) || equalsDownStation(station);
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

    public boolean isLongerThan(Section section) {
        return distance.isLongerThan(section.distance);
    }

    public int calculateDifferenceOfDistance(Section section) {
        return distance.calculateDifferenceBetween(section.distance);
    }

    public int calculateSumOfDistance(Section section) {
        return distance.calculateSumBetween(section.distance);
    }

    public Long getId() {
        return id.getId();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance.getDistance();
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
