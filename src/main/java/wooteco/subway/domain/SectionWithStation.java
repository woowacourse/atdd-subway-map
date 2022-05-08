package wooteco.subway.domain;

import java.util.Objects;

public class SectionWithStation {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public SectionWithStation() {
    }

    public SectionWithStation(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public SectionWithStation(Long lineId, Station upStation, Station downStation, int distance) {
        this(0L, lineId, upStation, downStation, distance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionWithStation sectionWithStation = (SectionWithStation) o;
        return distance == sectionWithStation.distance && Objects.equals(id, sectionWithStation.id) && lineId.equals(
                sectionWithStation.lineId)
                && upStation.equals(sectionWithStation.upStation) && downStation.equals(sectionWithStation.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "SectionWithStation{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
