package wooteco.subway.domain;

import java.util.HashMap;
import java.util.Map;

public class StationFactory {

    private static final Map<String, Station> cache;

    static {
        cache = new HashMap<>();
        cache.put("a", new Station(1L, "a"));
        cache.put("b", new Station(2L, "b"));
        cache.put("c", new Station(3L, "c"));
        cache.put("d", new Station(4L, "d"));
    }

    public static Station from(final String key) {
        return cache.get(key);
    }
}
