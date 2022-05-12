package wooteco.subway.repository.dao.entity;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.entity.line.LineEntity;
import wooteco.subway.repository.dao.entity.section.SectionEntity;
import wooteco.subway.repository.dao.entity.station.StationEntity;

public class EntityAssembler {

    private EntityAssembler() {
    }

    public static LineEntity lineEntity(Line line) {
        return new LineEntity(line.getId(), line.getName(), line.getColor());
    }

    public static Line line(LineEntity lineEntity, List<Section> sections) {
        return new Line(lineEntity.getId(), sections, lineEntity.getName(), lineEntity.getColor());
    }

    public static List<SectionEntity> sectionEntities(Long lineId, Line line) {
        return line.getSections()
                .stream()
                .map(section -> EntityAssembler.sectionEntity(lineId, section))
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

    public static StationEntity stationEntity(Station station) {
        return new StationEntity(station.getId(), station.getName());
    }

    public static Station station(StationEntity stationEntity) {
        return new Station(stationEntity.getId(), stationEntity.getName());
    }
}
