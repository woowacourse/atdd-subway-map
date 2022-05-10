package wooteco.subway.domain;

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
                '}';
    }
}
