package wooteco.subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.entity.SectionEntity;

public class SectionDistanceMap {

    private final Map<Long, Integer> upToDownDistanceMap;
    private final Map<Long, Integer> downToUpDistanceMap;

    private SectionDistanceMap(Map<Long, Integer> upToDownDistanceMap,
                               Map<Long, Integer> downToUpDistanceMap) {
        this.upToDownDistanceMap = upToDownDistanceMap;
        this.downToUpDistanceMap = downToUpDistanceMap;
    }

    public static SectionDistanceMap of(List<SectionEntity> sectionEntities) {
        Map<Long, Integer> upToDownDistanceMap = new HashMap<>();
        Map<Long, Integer> downToUpDistanceMap = new HashMap<>();

        for (SectionEntity section : sectionEntities) {
            int distance = section.getDistance();
            upToDownDistanceMap.put(section.getUpStationId(), distance);
            downToUpDistanceMap.put(section.getDownStationId(), distance);
        }
        return new SectionDistanceMap(upToDownDistanceMap, downToUpDistanceMap);
    }

    public int getCombinedDistanceOverStationOf(Long stationId) {
        return upToDownDistanceMap.get(stationId)
                + downToUpDistanceMap.get(stationId);
    }
}
