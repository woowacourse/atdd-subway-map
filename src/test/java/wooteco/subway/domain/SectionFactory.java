package wooteco.subway.domain;

import java.util.HashMap;
import java.util.Map;

public class SectionFactory {

    private static final Map<String, Section> cache;

    static {
        cache = new HashMap<>();
        cache.put("ab3", new Section(StationFactory.from("a"), StationFactory.from("b"), 3));
        cache.put("ac3", new Section(StationFactory.from("a"), StationFactory.from("c"), 3));
        cache.put("cb3", new Section(StationFactory.from("c"), StationFactory.from("b"), 3));
        cache.put("cd3", new Section(StationFactory.from ("c"), StationFactory.from("d"), 3));
        cache.put("ca3", new Section(StationFactory.from("c"), StationFactory.from("a"), 3));
        cache.put("ba3", new Section(StationFactory.from("b"), StationFactory.from("a"), 3));
        cache.put("bc3", new Section(StationFactory.from("b"), StationFactory.from("c"), 3));
    }

    public static Section from(final String key) {
        return cache.get(key);
    }
}
