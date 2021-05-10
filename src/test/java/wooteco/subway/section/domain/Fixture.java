package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

public class Fixture {
    static final Station GANGNAM_STATION = new Station(1L, "강남역");
    static final Station JAMSIL_STATION = new Station(2L, "잠실역");
    static final Station JONGHAP_STATION = new Station(3L, "종합운동장역");
    static final Station SADANG_STATION = new Station(4L, "사당역");
    static final Station DDUK_SUM_STATION = new Station(5L, "뚝섬역");

    static final Section FIRST_SECTION = new Section(GANGNAM_STATION, JAMSIL_STATION, 10);
    static final Section DOUBLE_END_UPSTATION_SECTION = new Section(JONGHAP_STATION, JAMSIL_STATION, 10);
    static final Section SECOND_SECTION = new Section(JAMSIL_STATION, JONGHAP_STATION, 10);
    static final Section THIRD_SECTION = new Section(JONGHAP_STATION, SADANG_STATION, 10);
    static final Section FOURTH_SECTION = new Section(SADANG_STATION, DDUK_SUM_STATION, 10);
}
