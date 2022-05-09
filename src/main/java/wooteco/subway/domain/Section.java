package wooteco.subway.domain;

public class Section {
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean canConnect(Section section) {
        return section.downStation.equals(this.upStation)
            || section.upStation.equals(this.downStation)
            || isSameUpStation(section)
            || isSameDownStation(section);
    }

    public boolean isSameUpStation(Section section) {
        return section.upStation.equals(this.upStation);
    }

    public boolean isSameDownStation(Section section) {
        return section.downStation.equals(this.downStation);
    }
}
