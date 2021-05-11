package wooteco.subway.dao.subway;

import java.util.HashMap;
import java.util.Map;

import static wooteco.subway.dao.fixture.Fixture.STATION3;
import static wooteco.subway.dao.fixture.Fixture.STATION4;
import static wooteco.subway.dao.fixture.StationAcceptanceTestFixture.insertStation;

public class SubwayAcceptanceTestFixture {
    public static Map<String, String> createAddSectionRequestWithAnotherSection() {
        insertStation(STATION3);
        insertStation(STATION4);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(3L));
        params.put("downStationId", String.valueOf(4L));
        params.put("distance", String.valueOf(3));
        return params;
    }

    public static Map<String, String> createAddSectionRequest() {
        insertStation(STATION3);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(1L));
        params.put("downStationId", String.valueOf(3L));
        params.put("distance", String.valueOf(3));
        return params;
    }

    public static Map<String, String> createAddSectionExpandUpStationRequest() {
        insertStation(STATION3);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(3L));
        params.put("downStationId", String.valueOf(1L));
        params.put("distance", String.valueOf(3));
        return params;
    }

    public static Map<String, String> createAddSectionExpandDownStationRequest() {
        insertStation(STATION3);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(2L));
        params.put("downStationId", String.valueOf(3L));
        params.put("distance", String.valueOf(3));
        return params;
    }

    public static Map<String, String> createAddSectionWithLongDistanceRequest() {
        insertStation(STATION3);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(1L));
        params.put("downStationId", String.valueOf(3L));
        params.put("distance", String.valueOf(100));
        return params;
    }

    public static Map<String, String> createAddSectionWithSameEndSectionsRequest() {
        insertStation(STATION3);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(1L));
        params.put("downStationId", String.valueOf(2L));
        params.put("distance", String.valueOf(100));
        return params;
    }
}
