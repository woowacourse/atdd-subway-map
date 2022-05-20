package wooteco.subway;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SubwayFixtures {

    public static final Station STATION_FIXTURE1 = new Station(1L, "선릉역");
    public static final Station STATION_FIXTURE2 = new Station(2L, "대림역");
    public static final Station 강남역 = new Station(1L, "강남역");
    public static final Station 역삼역 = new Station(2L, "역삼역");
    public static final Station 선릉역 = new Station(3L, "선릉역");
    public static final Station 삼성역 = new Station(4L, "삼성역");
    public static final Station 성담빌딩 = new Station(5L, "성담빌딩");
    public static final Station 서초역 = new Station(6L, "서초역");
    public static final Station 대림역 = new Station(7L, "대림역");

    public static Section YEOKSAM_TO_SUNNEUNG = new Section(1L, 2L, 선릉역, 역삼역, 10);
    public static Section GANGNAM_TO_YEOKSAM = new Section(2L, 2L, 역삼역, 강남역, 10);
    public static final Section SEOCHO_TO_GANGNAM = new Section(6L, 2L, 강남역, 서초역, 10);
    public static final Section DAELIM_TO_SEOCHO = new Section(7L, 2L, 서초역, 대림역, 10);
    public static final Section SUNNEUNG_TO_SUNGDAM = new Section(8L, 2L, 성담빌딩, 선릉역, 10);
}
