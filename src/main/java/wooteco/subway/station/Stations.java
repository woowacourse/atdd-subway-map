package wooteco.subway.station;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Stations {

    private final Map<Long, Station> stationGroup;

    public Stations(final Map<Long, Station> stationGroup) {
        this.stationGroup = new HashMap<>(stationGroup);
    }

    public Map<Long, Station> toMap() {
        return Collections.unmodifiableMap(stationGroup);
    }

    public Stream<Station> toStream() {
        return stationGroup.values().stream();
    }
}
