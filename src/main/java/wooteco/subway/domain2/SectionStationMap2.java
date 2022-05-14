package wooteco.subway.domain2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.entity.SectionEntity2;

public class SectionStationMap2 {

    private final Map<Long, Station> upToDownStationMap;
    private final Map<Long, Station> downToUpStationMap;

    private SectionStationMap2(Map<Long, Station> upToDownStationMap,
                               Map<Long, Station> downToUpStationMap) {
        this.upToDownStationMap = upToDownStationMap;
        this.downToUpStationMap = downToUpStationMap;
    }

    public static SectionStationMap2 of(List<SectionEntity2> sectionEntities) {
        Map<Long, Station> upToDownStationMap = new HashMap<>();
        Map<Long, Station> downToUpStationMap = new HashMap<>();

        for (SectionEntity2 sectionEntity : sectionEntities) {
            Long upStationId = sectionEntity.getUpStationId();
            Long downStationId = sectionEntity.getDownStationId();
            Station upStation = sectionEntity.getUpStation().toDomain();
            Station downStation = sectionEntity.getDownStation().toDomain();

            upToDownStationMap.put(upStationId, downStation);
            downToUpStationMap.put(downStationId,upStation);
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
