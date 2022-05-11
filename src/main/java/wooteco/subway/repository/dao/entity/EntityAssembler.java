package wooteco.subway.repository.dao.entity;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class EntityAssembler {

    public EntityAssembler() {
    }

    public static LineEntity lineEntity(Line line) {
        return new LineEntity(line.getId(), line.getName(), line.getColor());
    }

    public static Line line(LineEntity lineEntity) {
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    public static List<Line> lines(List<LineEntity> lineEntities) {
        return lineEntities.stream()
                .map(EntityAssembler::line)
                .collect(Collectors.toUnmodifiableList());
    }

    public static SectionEntity sectionEntity(Long lineId, Section section) {
        return new SectionEntity(section.getId(), lineId,
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance());
    }

    public static Section section(Station upStation, Station downStation, SectionEntity sectionEntity) {
        return new Section(sectionEntity.getId(),
                upStation, downStation,
                sectionEntity.getDistance());
    }

    public static List<Station> stations(List<StationEntity> stationEntities) {
        return stationEntities.stream()
                .map(EntityAssembler::station)
                .collect(Collectors.toUnmodifiableList());
    }

    public static StationEntity stationEntity(Station station) {
        return new StationEntity(station.getId(), station.getName());
    }

    public static Station station(StationEntity stationEntity) {
        return new Station(stationEntity.getId(), stationEntity.getName());
    }
}
