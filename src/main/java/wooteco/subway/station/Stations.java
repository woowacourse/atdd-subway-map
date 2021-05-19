package wooteco.subway.station;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stations {

    private final Map<Long, Station> stationGroup;

    public Stations(final List<Station> stations) {
        this.stationGroup = new LinkedHashMap<>();
        stationGroup.putAll(stations.stream()
            .collect(Collectors.toMap(Station::getId, Function.identity(),
                (existing, replacement) -> existing, LinkedHashMap::new)));
    }

    public Stream<Station> toStream() {
        return stationGroup.values().stream();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Stations stations = (Stations) o;
        return Objects.equals(stationGroup, stations.stationGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationGroup);
    }
}
