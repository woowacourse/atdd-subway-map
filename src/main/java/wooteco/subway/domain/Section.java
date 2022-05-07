package wooteco.subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    private Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this(upStation, downStation, distance);
        this.id = id;
        this.lineId = lineId;
    }

    private void validateSection(Station upStation, Station downStation) {
        if (upStation.isSameName(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
        }
    }

    public boolean hasSameUpStation(Section section) {
        return upStation.isSameName(section.upStation);
    }

    public boolean hasSameDownStation(Section section) {
        return downStation.isSameName(section.downStation);
    }

    public boolean isLongerThan(Section section) {
        return distance > section.distance;
    }

    public boolean hasSameUpStationWithOtherDownStation(Section section) {
        return upStation.isSameName(section.downStation);
    }

    public boolean hasSameDownStationWithOtherUpStation(Section section) {
        return downStation.isSameName(section.upStation);
    }

    public boolean isSameStations(Section section) {
        return upStation.isSameName(section.upStation) && downStation.isSameName(section.downStation);
    }

    public boolean isNotSameAnyStation(Section section) {
        return !upStation.isSameName(section.upStation) && !downStation.isSameName(section.downStation);
    }

    public Section splitSectionBySameUpStation(Section shorterSection) {
        return new Section(shorterSection.downStation, downStation, distance - shorterSection.distance);
    }

    public Section splitSectionBySameDownStation(Section shorterSection) {
        return new Section(upStation, shorterSection.upStation, distance - shorterSection.distance);
    }

    public Section mergeSectionByCut(Section downSection) {
        return new Section(upStation, downSection.downStation, distance + downSection.distance);
    }

    public boolean hasSameUpStationByStation(Station station) {
        return upStation.isSameName(station);
    }

    public boolean hasSameDownStationByStation(Station station) {
        return downStation.isSameName(station);
    }

    public Long getId() {
        return id;
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
}
