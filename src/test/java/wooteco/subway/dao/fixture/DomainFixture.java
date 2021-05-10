package wooteco.subway.dao.fixture;

import wooteco.subway.domain.Station;

import java.util.Arrays;
import java.util.List;

public class DomainFixture {
    public static final Station STATION1 = new Station(1L, "부산역");
    public static final Station STATION2 = new Station(2L, "서면역");
    public static final Station STATION3 = new Station(3L, "장산역");
    public static final Station STATION4 = new Station(4L, "주례역");
    public static final Station STATION5 = new Station(5L, "하단역");
    public static final List<Station> STATIONS = Arrays.asList(STATION1, STATION2);
}
