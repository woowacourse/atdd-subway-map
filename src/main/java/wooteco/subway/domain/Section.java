package wooteco.subway.domain;

import java.util.Objects;

public class Section {
    private static final String SAME_STATION_ERROR_MESSAGE = "상행과 하행 역은 동일할 수 없습니다.";
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        validateDistinctStation();
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
        this(0L, lineId, upStation, downStation, distance);
    }

    public static Section of(Section section, Station upStation, Station downStation) {
        return new Section(section.getId(), section.getLineId(), upStation, downStation,
                section.getDistance());
    }

    private void validateDistinctStation() {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_ERROR_MESSAGE);
        }
    }

    public Section toReverse() {
        return new Section(id, lineId, downStation, upStation, distance);
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

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    public Section calculateRemainSection(Section section, boolean isUpAttach) {
        if (isUpAttach) {
            return new Section(section.getLineId(), section.getDownStation(),
                    downStation, distance - section.getDistance());
        }
        return new Section(section.getLineId(), upStation,
                section.upStation, distance - section.getDistance());
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
        return distance == section.distance && Objects.equals(id, section.id) && lineId.equals(section.lineId)
                && upStation.equals(section.upStation) && downStation.equals(section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
