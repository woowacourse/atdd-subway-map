package wooteco.subway.fixture;

import java.util.HashMap;
import java.util.Map;

public class FixtureParams {
    public static Map<String, String> getLineParams() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        return params;
    }

    public static Map<String, String> getLineParams2() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "2호선");
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "5");
        return params;
    }

    public static Map<String, String> getStationParams() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        return params;
    }

    public static Map<String, String> getStationParams2() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "역삼역");
        return params;
    }
}
