package wooteco.subway;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class Fixtures {
    public static Station getStation(Long id, Station station) {
        return new Station(id, station.getName());
    }

    public static Line getLine(Long id, Line line) {
        return new Line(id, line.getName(), line.getColor(), line.getUpStationId(), line.getDownStationId(),
                line.getDistance());
    }

    public static Section getSection(Long id, Section section) {
        return new Section(id, section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }
}
