package wooteco.subway.entity;

import java.util.Objects;
import wooteco.subway.domain.line.LineInfo;
import wooteco.subway.domain.line.LineSection;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class LineSectionEntity {

    private final LineEntity lineEntity;
    private final StationEntity upStationEntity;
    private final StationEntity downStationEntity;
    private final int distance;

    public LineSectionEntity(LineEntity lineEntity,
                             StationEntity upStationEntity,
                             StationEntity downStationEntity,
                             int distance) {
        this.lineEntity = lineEntity;
        this.upStationEntity = upStationEntity;
        this.downStationEntity = downStationEntity;
        this.distance = distance;
    }

    public LineSection toDomain() {
        LineInfo line = lineEntity.toDomain();
        Station upStation = upStationEntity.toDomain();
        Station downStation = downStationEntity.toDomain();
        Section section = new Section(upStation, downStation, distance);
        return new LineSection(line, section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineSectionEntity that = (LineSectionEntity) o;
        return distance == that.distance
                && Objects.equals(lineEntity, that.lineEntity)
                && Objects.equals(upStationEntity, that.upStationEntity)
                && Objects.equals(downStationEntity, that.downStationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineEntity, upStationEntity, downStationEntity, distance);
    }

    @Override
    public String toString() {
        return "RegisteredSectionEntity{" +
                "lineEntity=" + lineEntity +
                ", upStationEntity=" + upStationEntity +
                ", downStationEntity=" + downStationEntity +
                ", distance=" + distance +
                '}';
    }
}
