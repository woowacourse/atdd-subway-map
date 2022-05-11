package wooteco.subway.domain;

public class Section {
    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private Section(Long id, Station upStation, Station downStation, int distance) {
        this(upStation, downStation, distance);
        this.id = id;
    }

    public static Section from(Long id, Section section) {
        return new Section(id, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public static Section from(Long id, Station upStation, Station downStation, int distance) {
        return new Section(id, upStation, downStation, distance);
    }

    public static Section from(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isLongerThan(int distance) {
        return this.distance > distance;
    }

    public void updateUpStation(Station station, int distance) {
        this.upStation = station;
        this.distance = distance;
    }

    public void updateDownStation(Station station, int distance) {
        this.downStation = station;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Section section = (Section)o;

        if (getDistance() != section.getDistance())
            return false;
        if (getUpStation() != null ? !getUpStation().equals(section.getUpStation()) : section.getUpStation() != null)
            return false;
        return getDownStation() != null ? getDownStation().equals(section.getDownStation()) :
            section.getDownStation() == null;
    }

    @Override
    public int hashCode() {
        int result = getUpStation() != null ? getUpStation().hashCode() : 0;
        result = 31 * result + (getDownStation() != null ? getDownStation().hashCode() : 0);
        result = 31 * result + getDistance();
        return result;
    }
}
