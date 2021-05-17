package wooteco.subway.fixture;

import wooteco.subway.domain.Station;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static wooteco.subway.fixture.Fixture.*;
import static wooteco.subway.fixture.StationAcceptanceTestFixture.insertStations;

public class LineAcceptanceTestFixture {

    public static Map<String, String> createLineWithSection(List<Station> stations) {
        List<Station> sortedStations = sortStationById(stations);
        insertStations(sortedStations); // 역을 먼저 Station 테이블에 삽입함.
        return createLineRequest(LINE_COLOR1, LINE_NAME1, sortedStations.get(0).getId(), sortedStations.get(1).getId(), DEFAULT_DISTANCE);
    }

    public static Map<String, String> createLineWithExistName(List<Station> stations) {
        List<Station> sortedStations = sortStationById(stations);
        insertStations(sortedStations); // 역을 먼저 Station 테이블에 삽입함.
        return createLineRequest(LINE_COLOR2, LINE_NAME1, sortedStations.get(0).getId(), sortedStations.get(1).getId(), DEFAULT_DISTANCE);
    }

    public static Map<String, String> createLineWithExistColor(List<Station> stations) {
        List<Station> sortedStations = sortStationById(stations);
        insertStations(sortedStations); // 역을 먼저 Station 테이블에 삽입함.
        return createLineRequest(LINE_COLOR1, LINE_NAME2, sortedStations.get(0).getId(), sortedStations.get(1).getId(), DEFAULT_DISTANCE);
    }

    // 역의 아이디로 정렬
    private static List<Station> sortStationById(List<Station> stations) {
        return stations.stream()
                .sorted(Comparator.comparing(Station::getId))
                .collect(Collectors.toList());
    }

    public static Map<String, String> createLineRequest(String color, String name, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }
}
