package wooteco.subway.domain.fixture;

import wooteco.subway.domain.station.Station;

public class StationFixture {

    public static Station getStationA() {
        return new Station(1L, "왕십리역");
    }

    public static Station getStationB() {
        return new Station(2L, "마장역");
    }

    public static Station getStationC() {
        return new Station(3L, "답십리역");
    }

    public static Station getStationX() {
        return new Station(4L, "장한평역");
    }

    public static Station getStationY() {
        return new Station(5L, "관련없는역");
    }
}
