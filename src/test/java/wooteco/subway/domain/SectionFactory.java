package wooteco.subway.domain;

import java.util.HashMap;
import java.util.Map;

public class SectionFactory {

    private static final Map<String, Section> cache;

    static {
        cache = new HashMap<>();
        cache.put("1a2b3", new Section(StationFactory.from("a"), StationFactory.from("b"), 3));
        cache.put("1a3c3", new Section(StationFactory.from("a"), StationFactory.from("c"), 3));
        cache.put("3c2b3", new Section(StationFactory.from("c"), StationFactory.from("b"), 3));
        cache.put("3c4d3", new Section(StationFactory.from ("c"), StationFactory.from("d"), 3));
    }

    public static Section from(final String key) {
        return cache.get(key);
    }
}
