package wooteco.subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.entity.SectionEntity;

public class SectionDistanceMap {

    private static final String LONGER_THAN_PREVIOUS_SECTION_EXCEPTION = "기존 등록된 구간보다 긴 거리를 입력하면 안됩니다.";

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

    public int getRemainderDistanceToDownStation(int distance, Long upStationId) {
        if (distance >= upToDownDistanceMap.get(upStationId)) {
            throw new IllegalArgumentException(LONGER_THAN_PREVIOUS_SECTION_EXCEPTION);
        }
        return upToDownDistanceMap.get(upStationId) - distance;
    }

    public int getRemainderDistanceToUpStation(int distance, Long downStationId) {
        if (distance >= downToUpDistanceMap.get(downStationId)) {
            throw new IllegalArgumentException(LONGER_THAN_PREVIOUS_SECTION_EXCEPTION);
        }
        return downToUpDistanceMap.get(downStationId) - distance;
    }
}
