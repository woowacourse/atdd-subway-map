package wooteco.subway;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SubwayFixtures {

    public static final Station STATION_FIXTURE1 = new Station(1L, "선릉역");
    public static final Station STATION_FIXTURE2 = new Station(2L, "대림역");
    public static final Station GANGNAM = new Station(1L, "강남역");
    public static final Station YEOKSAM = new Station(2L, "역삼역");
    public static final Station SUNNEUNG = new Station(3L, "선릉역");
    public static final Station SAMSUNG = new Station(4L, "삼성역");
    public static final Station SUNGDAM = new Station(5L, "성담빌딩");
    public static Section YEOKSAM_TO_SUNNEUNG = new Section(1L, 2L, SUNNEUNG, YEOKSAM, 10);
    public static Section GANGNAM_TO_YEOKSAM = new Section(2L, 2L, YEOKSAM, GANGNAM, 10);
    public static Section SUNNEUNG_TO_SAMSUNG = new Section(3L, 2L, SAMSUNG, SUNNEUNG, 10);
    public static Section SUNGDAM_BUILDING_TO_SAMSUNG = new Section(4L, 2L, SAMSUNG, SUNGDAM, 5);
    public static Section YEOKSAM_TO_SAMSUNG = new Section(5L, 2L, SAMSUNG, YEOKSAM, 10);
}
