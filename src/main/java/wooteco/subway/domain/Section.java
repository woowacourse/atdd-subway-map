package wooteco.subway.domain;

public class Section {

    private Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section createWithId(Long id, Station upStation, Station downStation, int distance) {
        return new Section(id, upStation, downStation, distance);
    }

    public static Section createWithoutId(Station upStation, Station downStation, int distance) {
        return new Section(null, upStation, downStation, distance);
    }

    public boolean isEqualToUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualToDownStation(Station station) {
        return downStation.equals(station);
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
