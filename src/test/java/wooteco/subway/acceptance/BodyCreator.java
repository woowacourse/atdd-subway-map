package wooteco.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

public class BodyCreator {
    static Map<String, String> makeLineBodyForPost(String name, String color, String upStationId, String downStationId,
        String distance) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("color", color);
        body.put("upStationId", upStationId);
        body.put("downStationId", downStationId);
        body.put("distance", distance);
        return body;
    }

    static Map<String, String> makeStationBodyForPost(String name) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        return body;
    }
}
