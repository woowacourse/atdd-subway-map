package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getLineId() {
        return lineId;
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

    public boolean isUpperThan(final Section otherSection) {
        return this.downStation.equals(otherSection.upStation);
    }

    public boolean isLowerThan(final Section otherSection) {
        return this.upStation.equals(otherSection.downStation);
    }

    public boolean isConnectedSection(final Section otherSection) {
        return contain(otherSection.upStation) || contain(otherSection.downStation);
    }

    private boolean contain(final Station station) {
        return this.upStation.equals(station) || this.downStation.equals(station);
    }

    public boolean equalsWithUpStation(final Section otherSection) {
        return this.upStation.equals(otherSection.upStation) || this.upStation.equals(otherSection.downStation);
    }

    public boolean equalsWithDownStation(final Section otherSection) {
        return this.downStation.equals(otherSection.upStation) || this.downStation.equals(otherSection.downStation);
    }

    public boolean hasSameUpStation(final Section otherSection) {
        return this.upStation.equals(otherSection.upStation);
    }

    public void updateSectionWithSameUpStation(final Section otherSection) {
        this.upStation = otherSection.downStation;
        this.distance = this.distance - otherSection.distance;
    }

    public boolean hasSameDownStationWith(final Section otherSection) {
        return this.downStation.equals(otherSection.downStation);
    }

    public void updateSectionWithSameDownStation(final Section otherSection) {
        this.downStation = otherSection.upStation;
        this.distance = this.distance - otherSection.distance;
    }
}
