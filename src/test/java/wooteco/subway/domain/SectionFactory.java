package wooteco.subway.domain;

import java.util.HashMap;
import java.util.Map;

public class SectionFactory {

    private static final Map<String, Section> cache;

    static {
        cache = new HashMap<>();
        cache.put("1a2b", new Section(new Station(1L, "a"), new Station(2L, "b")));
        cache.put("1a3c", new Section(new Station(1L, "a"), new Station(3L, "c")));
        cache.put("3c2b", new Section(new Station(3L, "c"), new Station(2L, "b")));
        cache.put("3c4d", new Section(new Station(3L, "c"), new Station(4L, "d")));
    }

    public static Section from(final String key) {
        return cache.get(key);
    }
}
