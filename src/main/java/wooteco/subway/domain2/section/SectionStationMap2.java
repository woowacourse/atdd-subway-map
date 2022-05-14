package wooteco.subway.domain2.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.domain2.station.Station;

public class SectionStationMap2 {

    private final Map<Long, Station> upToDownStationMap;
    private final Map<Long, Station> downToUpStationMap;

    private SectionStationMap2(Map<Long, Station> upToDownStationMap,
                               Map<Long, Station> downToUpStationMap) {
        this.upToDownStationMap = upToDownStationMap;
        this.downToUpStationMap = downToUpStationMap;
    }

    public static SectionStationMap2 of(List<Section> sections) {
        Map<Long, Station> upToDownStationMap = new HashMap<>();
        Map<Long, Station> downToUpStationMap = new HashMap<>();

        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            upToDownStationMap.put(upStation.getId(), downStation);
            downToUpStationMap.put(downStation.getId(),upStation);
        }
        return new SectionStationMap2(upToDownStationMap, downToUpStationMap);
    }

    public Long findAnyStationId() {
        return (Long) upToDownStationMap.keySet().toArray()[0];
    }

    public boolean hasUpStation(Long downStationId) {
        return downToUpStationMap.containsKey(downStationId);
    }

    public boolean hasDownStation(Long upStationId) {
        return upToDownStationMap.containsKey(upStationId);
    }

    public Long getDownStationIdOf(Long upStationId) {
        return upToDownStationMap.get(upStationId).getId();
    }

    public Long getUpStationIdOf(Long downStationId) {
        return downToUpStationMap.get(downStationId).getId();
    }
}
