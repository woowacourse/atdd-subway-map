package wooteco.subway.domain.factory;

import static wooteco.subway.domain.factory.StationFactory.A;
import static wooteco.subway.domain.factory.StationFactory.B;
import static wooteco.subway.domain.factory.StationFactory.C;
import static wooteco.subway.domain.factory.StationFactory.D;

import java.util.HashMap;
import java.util.Map;
import wooteco.subway.domain.Section;

public class SectionFactory {

    public static final String AB3 = "ab3";
    public static final String AC3 = "ac3";
    public static final String CB3 = "cb3";
    public static final String CD3 = "cd3";
    public static final String CA3 = "ca3";
    public static final String BA3 = "ba3";
    public static final String BC3 = "bc3";
    public static final String AC2 = "ac2";
    public static final String AC1 = "ac1";
    public static final String CB1 = "cb1";
    public static final String CB2 = "cb2";

    private static final Map<String, Section> cache;

    static {
        cache = new HashMap<>();
        cache.put(AB3, new Section(StationFactory.from(A), StationFactory.from(B), 3));
        cache.put(AC3, new Section(StationFactory.from(A), StationFactory.from(C), 3));
        cache.put(CB3, new Section(StationFactory.from(C), StationFactory.from(B), 3));
        cache.put(CD3, new Section(StationFactory.from (C), StationFactory.from(D), 3));
        cache.put(CA3, new Section(StationFactory.from(C), StationFactory.from(A), 3));
        cache.put(BA3, new Section(StationFactory.from(B), StationFactory.from(A), 3));
        cache.put(BC3, new Section(StationFactory.from(B), StationFactory.from(C), 3));
        cache.put(AC2, new Section(StationFactory.from(A), StationFactory.from(C), 2));
        cache.put(AC1, new Section(StationFactory.from(A), StationFactory.from(C), 1));
        cache.put(CB1, new Section(StationFactory.from(C), StationFactory.from(B), 1));
        cache.put(CB2, new Section(StationFactory.from(C), StationFactory.from(B), 2));
    }

    public static Section from(final String key) {
        return cache.get(key);
    }
}
