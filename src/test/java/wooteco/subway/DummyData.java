package wooteco.subway;

import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

public class DummyData {
    // Station
    public static Station 왕십리역 = new Station(1L, "왕십리역");
    public static Station 잠실역 = new Station(2L, "잠실역");
    public static Station 강남역 = new Station(3L, "강남역");

    // Line
    public static Line 신분당선 = new Line(1L,"신분당선", "yellow");
    public static Line 이호선 = new Line(2L,"2호선", "green");
    public static Line 사호선 = new Line(3L,"4호선", "sky");

    // Section
    public static Section 왕십리_잠실 = new Section(1L, 이호선, 왕십리역, 잠실역, 10);
    public static Section 잠실_강남 = new Section(2L, 이호선, 잠실역, 강남역, 10);
}
