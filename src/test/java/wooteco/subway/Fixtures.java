package wooteco.subway;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class Fixtures {
    public static final Station STATION = new Station("강남역");
    public static final Station STATION_2 = new Station("선릉역");
    public static final Station STATION_3 = new Station("잠실역");

    public static final Line LINE = new Line("신분당선", "red");
    public static final Line LINE_2 = new Line("분당선", "green");

    public static final Section SECTION = new Section(1L, 1L, 2L, 10);
    public static final Section SECTION_2 = new Section(1L, 2L, 3L, 5);

    public static Station getStation(Long id, Station station) {
        return new Station(id, station.getName());
    }

    public static Line getLine(Long id, Line line) {
        return new Line(id, line.getName(), line.getColor());
    }

    public static Section getSection(Long id, Section section) {
        return new Section(id, section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }
}
