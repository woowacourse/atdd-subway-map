package wooteco.subway.domain;

public class Section {
    private Long id;
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
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean canInsert(Section section) {
        return section.distance < this.distance;
    }

    public void changeUpStationAndDistance(Section section) {
        this.upStation = section.downStation;
        this.distance -= section.distance;
    }

    public void changeDownStationAndDistance(Section section) {
        this.downStation = section.upStation;
        this.distance -= section.distance;
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}
