package wooteco.subway;

import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

public class DummyData {
    // Station
    public static Station 왕십리역 = new Station(1L, "왕십리역");
    public static Station 잠실역 = new Station(2L, "잠실역");
    public static Station 강남역 = new Station(3L, "강남역");
    public static Station 구의역 = new Station(4L, "구의역");
    public static Station 건대입구역 = new Station(5L, "건대입구");
    public static Station 한양대역 = new Station(6L, "한양대");

    // Line
    public static Line 신분당선 = new Line(1L, "신분당선", "yellow");
    public static Line 이호선 = new Line(2L, "2호선", "green");
    public static Line 사호선 = new Line(3L, "4호선", "sky");

    // Section
    public static Section 이호선_왕십리_잠실_거리10 = new Section(1L, 이호선, 왕십리역, 잠실역, 10);
    public static Section 이호선_잠실_강남_거리5 = new Section(2L, 이호선, 잠실역, 강남역, 5);
    public static Section 이호선_강남_구의_거리7 = new Section(3L, 이호선, 강남역, 구의역, 7);
    public static Section 이호선_구의_건대입구_거리3 = new Section(3L, 이호선, 구의역, 건대입구역, 3);
    public static Section 이호선_왕십리_한양대_거리3 = new Section(3L, 이호선, 왕십리역, 한양대역, 3);
    public static Section 이호선_한양대_건대입구_거리5 = new Section(3L, 이호선, 한양대역, 건대입구역, 5);
    public static Section 이호선_왕십리_구의_거리5 = new Section(3L, 이호선, 왕십리역, 구의역, 5);
}
