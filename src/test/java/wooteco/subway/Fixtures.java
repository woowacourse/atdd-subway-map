package wooteco.subway;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class Fixtures {
    public static final Station STATION = new Station(1L, "강남역");
    public static final Station STATION_2 = new Station(2L, "선릉역");
    public static final Station STATION_3 = new Station(3L, "잠실역");
    public static final Station STATION_4 = new Station(4L, "사당역");

    public static final Line LINE = new Line(1L, "신분당선", "red");
    public static final Line LINE_2 = new Line(1L, "분당선", "green");

    public static final Section SECTION = new Section(1L, STATION, STATION_2, 10);
    public static final Section SECTION_2 = new Section(1L, STATION_2, STATION_3, 5);
    public static final Section SECTION_3 = new Section(1L, STATION_2, STATION_4, 3);
    public static final Section SECTION_4 = new Section(1L, STATION_3, STATION_4, 3);
    public static final Section SECTION_5 = new Section(1L, STATION_2, STATION_4, 10);

    public static Station getStation(Long id, Station station) {
        return new Station(id, station.getName());
    }

    public static Line getLine(Long id, Line line) {
        return new Line(id, line.getName(), line.getColor());
    }

    public static Section getSection(Long id, Section section) {
        return new Section(id, section.getLineId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance());
    }
}
