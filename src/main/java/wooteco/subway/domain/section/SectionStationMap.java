package wooteco.subway.domain.section;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wooteco.subway.domain.station.Station;

public class SectionStationMap {

    private final Map<Long, Station> upToDownStationMap;
    private final Map<Long, Station> downToUpStationMap;

    private SectionStationMap(Map<Long, Station> upToDownStationMap,
                              Map<Long, Station> downToUpStationMap) {
        this.upToDownStationMap = upToDownStationMap;
        this.downToUpStationMap = downToUpStationMap;
    }

    public static SectionStationMap of(List<Section> sections) {
        Map<Long, Station> upToDownStationMap = new HashMap<>();
        Map<Long, Station> downToUpStationMap = new HashMap<>();

        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            upToDownStationMap.put(upStation.getId(), downStation);
            downToUpStationMap.put(downStation.getId(),upStation);
        }
        return new SectionStationMap(upToDownStationMap, downToUpStationMap);
    }

    public Station findUpperEndStation() {
        Set<Station> upStations = new HashSet<>(downToUpStationMap.values());
        Set<Station> downStations = new HashSet<>(upToDownStationMap.values());

        upStations.removeAll(downStations);
        return upStations.stream()
                .findFirst()
                .get();
    }

    public boolean hasDownStation(Long upStationId) {
        return upToDownStationMap.containsKey(upStationId);
    }

    public Station getDownStationIdOf(Long upStationId) {
        return upToDownStationMap.get(upStationId);
    }
}
