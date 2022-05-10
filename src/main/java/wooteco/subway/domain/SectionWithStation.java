package wooteco.subway.domain;

import java.util.Objects;

public class SectionWithStation {
    private static final String SAME_STATION_ERROR_MESSAGE = "상행과 하행 역은 동일할 수 없습니다.";
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
        validateDistinctStation();
    }

    public SectionWithStation(Long lineId, Station upStation, Station downStation, int distance) {
        this(0L, lineId, upStation, downStation, distance);
    }

    public static SectionWithStation of(Section section, Station upStation, Station downStation) {
        return new SectionWithStation(section.getId(), section.getLineId(), upStation, downStation,
                section.getDistance());
    }

    private void validateDistinctStation() {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_ERROR_MESSAGE);
        }
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

    public Section toEntity() {
        return new Section(lineId, upStation.getId(), downStation.getId(), distance);
    }
}
