package wooteco.subway.dao.subway;

import wooteco.subway.domain.Station;

import java.util.HashMap;
import java.util.Map;

import static wooteco.subway.dao.fixture.DomainFixture.STATION3;
import static wooteco.subway.dao.fixture.StationAcceptanceTestFixture.insertStation;

public class SubwayAcceptanceTestFixture {
    public static Map<String, String> createAddSectionRequest() {
        insertStation(STATION3);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(1L));
        params.put("downStationId", String.valueOf(3L));
        params.put("distance", String.valueOf(3));
        return params;
    }
}
