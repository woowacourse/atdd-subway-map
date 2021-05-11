package wooteco.subway.station.fixture;

import wooteco.subway.station.controller.StationRequest;
import wooteco.subway.station.domain.Station;

public class StationFixture {
    public static final Station GANGNAM_STATION = new Station(1L, "강남역");
    public static final Station JAMSIL_STATION = new Station(2L, "잠실역");
    public static final Station JONGHAP_STATION = new Station(3L, "종합운동장역");
    public static final Station SADANG_STATION = new Station(4L, "사당역");
    public static final Station DDUK_SUM_STATION = new Station(5L, "뚝섬역");
    public static final Station BANGBAE_STATION = new Station(6L, "방배역");
    public static final Station SAMSUNG_STATION = new Station(7L, "삼성역");
    public static final Station GYODAE_STATION = new Station(8L, "교대역");
    public static final Station ANYANG_STATION = new Station(9L, "안양역");
    public static final Station YEOKSAM_STATION = new Station(10L, "역삼역");

    public static final StationRequest GANG_SAM_STATION_REQUEST = new StationRequest(GANGNAM_STATION);
    public static final StationRequest JAM_SIL_STATION_REQUEST = new StationRequest(JAMSIL_STATION);
    public static final StationRequest YEOK_SAM_STATION_REQUEST = new StationRequest(YEOKSAM_STATION);
}
