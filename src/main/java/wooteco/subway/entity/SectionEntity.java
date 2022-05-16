package wooteco.subway.entity;

import java.util.Objects;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class SectionEntity {

    private final Long lineId;
    private final StationEntity upStation;
    private final StationEntity downStation;
    private final int distance;

    public SectionEntity(Long lineId,
                         StationEntity upStation,
                         StationEntity downStation,
                         int distance) {
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionEntity of(Long lineId, Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        StationEntity upStationEntity = new StationEntity(upStation.getId(), upStation.getName());
        StationEntity downStationEntity = new StationEntity(downStation.getId(), downStation.getName());
        int distance = section.getDistance();

        return new SectionEntity(lineId, upStationEntity, downStationEntity, distance);
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    public Section toDomain() {
        return new Section(lineId, upStation.toDomain(), downStation.toDomain(), distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionEntity that = (SectionEntity) o;
        return distance == that.distance
                && Objects.equals(lineId, that.lineId)
                && Objects.equals(upStation, that.upStation)
                && Objects.equals(downStation, that.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "SectionEntity{" +
                "lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
