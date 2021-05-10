package wooteco.subway.station;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stations {

    private final Map<Long, Station> stationGroup;

    public Stations(final Map<Long, Station> stationGroup) {
        this.stationGroup = new LinkedHashMap<>(stationGroup);
    }

    public void addStations(final List<Station> stations) {
        stationGroup.putAll(stations.stream()
            .collect(Collectors.toMap(Station::getId, Function.identity(),
                (existing, replacement) -> existing, LinkedHashMap::new)));
    }

    public Map<Long, Station> toMap() {
        return Collections.unmodifiableMap(stationGroup);
    }

    public Stream<Station> toStream() {
        return stationGroup.values().stream();
    }
}
