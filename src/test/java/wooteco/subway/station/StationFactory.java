package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import wooteco.subway.station.domain.Station;

@DisplayName("기본 역 정의")
public class StationFactory {

    public static final Station 흑기역 = new Station(1L, "흑기역");
    public static final Station 백기역 = new Station(2L, "백기역");
    public static final Station 낙성대역 = new Station(3L, "낙성대역");
    public static final Station 잠실역 = new Station(4L, "잠실역");
}
