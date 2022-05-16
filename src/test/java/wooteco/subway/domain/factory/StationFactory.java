package wooteco.subway.domain.factory;

import java.util.HashMap;
import java.util.Map;
import wooteco.subway.domain.Station;

public class StationFactory {

    public static final String A = "a";
    public static final String B = "b";
    public static final String C = "c";
    public static final String D = "d";

    private static final Map<String, Station> cache;

    static {
        cache = new HashMap<>();
        cache.put(A, new Station(1L, A));
        cache.put(B, new Station(2L, B));
        cache.put(C, new Station(3L, C));
        cache.put(D, new Station(4L, D));
    }

    public static Station from(final String key) {
        return cache.get(key);
    }
}
