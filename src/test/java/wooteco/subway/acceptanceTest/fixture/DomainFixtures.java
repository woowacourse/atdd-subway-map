package wooteco.subway.acceptanceTest.fixture;

import wooteco.subway.domain.station.Station;

public class DomainFixtures {
    public static final Station STATION_1 = new Station(1L, "첫 번째 역");
    public static final Station STATION_2 = new Station(2L, "두 번째 역");
    public static final Station STATION_3 = new Station(3L, "세 번째 역");
    public static final Station STATION_4 = new Station(4L, "네 번째 역");
    public static final Station NEW_STATION = new Station(5L, "새로운 역");
    public static final Long LINE_ID = 1L;
    public static final String LINE_NAME = "노선1";
    public static final String LINE_COLOR = "노선1의 색깔";
    public static final Long NEW_STATION_ID = 5L;
    public static final String NEW_STATION_NAME = "새로운 역";
    public static final int NEW_SECTION_DISTANCE = 14;
    public static final int DEFAULT_SECTION_DISTANCE = 20;
}
