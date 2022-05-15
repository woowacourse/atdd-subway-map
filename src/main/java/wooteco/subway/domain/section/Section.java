package wooteco.subway.domain.section;

import java.util.List;

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

    public boolean containsUpStationOf(Section section) {
        return equalsUpStation(section) || isPreviousOf(section);
    }

    public boolean containsDownStationOf(Section section) {
        return equalsDownStation(section) || isNextOf(section);
    }

    public boolean isPreviousOf(Section section) {
        return downStation.equals(section.upStation);
    }

    public boolean isNextOf(Section section) {
        return upStation.equals(section.downStation);
    }

    public boolean equalsUpStation(Section section) {
        return containsAsUpStation(section.upStation);
    }

    public boolean containsAsUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalsDownStation(Section section) {
        return containsAsDownStation(section.downStation);
    }

    public boolean containsAsDownStation(Station station) {
        return downStation.equals(station);
    }

    public List<Section> split(Section section) {
        if (equalsUpStation(section)) {
            Section fragment = new Section(section.downStation, downStation, subtractDistance(section));
            return List.of(section, fragment);
        }
        if (equalsDownStation(section)) {
            Section fragment = new Section(upStation, section.upStation, subtractDistance(section));
            return List.of(fragment, section);
        }
        throw new IllegalArgumentException("상행역과 하행역이 일치하지 않습니다.");
    }

    private int subtractDistance(Section section) {
        validateDistanceIsEnoughToSplit(section);
        return distance.subtract(section.distance);
    }

    private void validateDistanceIsEnoughToSplit(Section section) {
        if (!distance.isLongerThan(section.distance)) {
            throw new IllegalArgumentException("기존 구간의 거리보다 길거나 같습니다.");
        }
    }

    public Section merge(Section section) {
        if (isPreviousOf(section)) {
            return new Section(upStation, section.downStation, sumDistance(section));
        }
        if (isNextOf(section)) {
            return new Section(section.upStation, downStation, sumDistance(section));
        }
        throw new IllegalArgumentException("두 구간은 이어지지 않았습니다.");
    }

    private int sumDistance(Section section) {
        return distance.sum(section.distance);
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
