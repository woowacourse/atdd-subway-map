package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private Long distance;

    public Section(Long id, Long lineId, Station upStation, Station downStation, Long distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long lineId, Station upStation, Station downStation, Long distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Section> putBetweenUpStation(Section section) {
        validateDistance(section);
        Section upperSection = new Section(this.getLineId(), getUpStation(), section.getDownStation(), section.getDistance());
        Section lowerSection = new Section(this.getLineId(), section.getDownStation(), getDownStation(),
                getDistance() - section.getDistance());

        ArrayList<Section> results = new ArrayList<>() {
            {
                add(upperSection);
                add(lowerSection);
            }
        };

        return results;
    }

    public List<Section> putBetweenDownStation(Section section) {
        validateDistance(section);
        Section upperSection = new Section(this.getLineId(), getDownStation(), section.getUpStation(),
                section.getDistance());
        Section lowerSection = new Section(this.getLineId(), section.getUpStation(), getUpStation(),
                getDistance() - section.getDistance());

        ArrayList<Section> results = new ArrayList<>() {
            {
                add(upperSection);
                add(lowerSection);
            }
        };

        return results;
    }

    private void validateDistance(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("새로 등록되는 구간은 기존의 구간의 거리보다 같거나 커서는 안됩니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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

    public Long getDistance() {
        return distance;
    }

    public String getUpStationName() {
        return upStation.getName();
    }

    public String getDownStationName() {
        return downStation.getName();
    }

    public boolean contains(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                ", distance = " + getDistance() +
                '}';
    }
}
