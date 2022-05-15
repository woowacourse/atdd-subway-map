package wooteco.subway.domain.line;

import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.station.RegisteredStation;
import wooteco.subway.domain.station.Station;

public class SubwayMaps {

    private final Map<Long, SubwayMap> value;

    private SubwayMaps(Map<Long, SubwayMap> value) {
        this.value = value;
    }

    public static SubwayMaps of(List<RegisteredStation> stations) {
        Map<Long, SubwayMap> value = new HashMap<>();
        Map<Long, List<RegisteredStation>> registeredLines = groupStationsByLineId(stations);
        for (Long lineId : registeredLines.keySet()) {
            List<RegisteredStation> sameLineStations = registeredLines.get(lineId);
            Line line = toLine(sameLineStations);
            List<Station> registeredStations = toStations(sameLineStations);
            value.put(lineId, new SubwayMap(line, registeredStations));
        }
        return new SubwayMaps(value);
    }

    private static Map<Long, List<RegisteredStation>> groupStationsByLineId(List<RegisteredStation> stations) {
        return stations.stream()
                .collect(groupingBy(RegisteredStation::getLineId));
    }

    private static Line toLine(List<RegisteredStation> registeredStations) {
        return registeredStations.get(0).getLine();
    }

    private static List<Station> toStations(List<RegisteredStation> registeredStations) {
        return registeredStations.stream()
                .map(RegisteredStation::getStation)
                .collect(Collectors.toList());
    }

    public List<SubwayMap> toList() {
        return new ArrayList<>(value.values());
    }

    @Override
    public String toString() {
        return "SubwayMaps{" + "value=" + value + '}';
    }
}
