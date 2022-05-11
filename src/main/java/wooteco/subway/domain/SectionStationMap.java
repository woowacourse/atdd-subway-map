package wooteco.subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionStationMap {

    private final Map<Long, Long> upToDownStationIdMap;
    private final Map<Long, Long> downToUpStationIdMap;

    private SectionStationMap(Map<Long, Long> upToDownStationIdMap,
                              Map<Long, Long> downToUpStationIdMap) {
        this.upToDownStationIdMap = upToDownStationIdMap;
        this.downToUpStationIdMap = downToUpStationIdMap;
    }

    public static SectionStationMap of(List<SectionStationInfo> sectionStationInfos) {
        Map<Long, Long> upToDownStationIdMap = new HashMap<>();
        Map<Long, Long> downToUpStationIdMap = new HashMap<>();

        for (SectionStationInfo sectionStationInfo : sectionStationInfos) {
            Long upStationId = sectionStationInfo.getUpStationId();
            Long downStationId = sectionStationInfo.getDownStationId();

            upToDownStationIdMap.put(upStationId, downStationId);
            downToUpStationIdMap.put(downStationId, upStationId);
        }
        return new SectionStationMap(upToDownStationIdMap, downToUpStationIdMap);
    }

    public Long findAnyStationId() {
        return (Long) upToDownStationIdMap.keySet().toArray()[0];
    }

    public boolean hasUpStation(Long downStationId) {
        return downToUpStationIdMap.containsKey(downStationId);
    }

    public boolean hasDownStation(Long upStationId) {
        return upToDownStationIdMap.containsKey(upStationId);
    }

    public Long getDownStationIdOf(Long upStationId) {
        return upToDownStationIdMap.get(upStationId);
    }

    public Long getUpStationIdOf(Long downStationId) {
        return downToUpStationIdMap.get(downStationId);
    }

    public int getSize() {
        return upToDownStationIdMap.size();
    }

    public boolean isFinalUpStation(Long stationId) {
       return !hasUpStation(stationId) && hasDownStation(stationId);
    }

    public boolean isFinalDownStation(Long stationId) {
        return hasUpStation(stationId) && !hasDownStation(stationId);
    }
}
