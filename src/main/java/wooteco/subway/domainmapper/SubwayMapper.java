package wooteco.subway.domainmapper;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;

@Component
public class SubwayMapper {

    public Station station(StationEntity stationEntity) {
        return new Station(stationEntity.getId(), stationEntity.getName());
    }

    public Line line(LineEntity lineEntity, Sections sections) {
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), sections);
    }

    public Section section(SectionEntity sectionEntity, Station upStation, Station downStation) {
        return new Section(sectionEntity.getId(), upStation, downStation,
            new Distance(sectionEntity.getDistance()));
    }
}
