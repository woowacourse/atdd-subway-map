package wooteco.subway.domain2;

import java.util.LinkedList;
import java.util.List;
import wooteco.subway.entity.SectionEntity2;

// TODO: names should be changed
public class SectionViews2 {

    private final SectionStationMap2 sectionMap;
    private final StationMap entityMap;

    private SectionViews2(SectionStationMap2 sectionMap,
                         StationMap entityMap) {
        this.sectionMap = sectionMap;
        this.entityMap = entityMap;
    }

    public static SectionViews2 of(List<SectionEntity2> sectionEntities) {
        return new SectionViews2(
                SectionStationMap2.of(sectionEntities),
                StationMap.of(sectionEntities));
    }

    public List<Station> getSortedStationsList() {
        Long initialStationId = sectionMap.findAnyStationId();
        boolean isDownDirection = true;
        int stationEntityCount = entityMap.getSize();

        return toSortedStationList(initialStationId, isDownDirection, stationEntityCount);
    }

    private List<Station> toSortedStationList(Long initialStationId,
                                                    boolean isDownDirection,
                                                    int capacity) {
        LinkedList<Station> list = new LinkedList<>();
        list.add(entityMap.findEntityOfId(initialStationId));
        Long currentStationId = initialStationId;

        while (list.size() < capacity) {
            if (isDownDirection && sectionMap.hasDownStation(currentStationId)) {
                currentStationId = addDownStationIdAndGetNextKey(list, currentStationId);
                continue;
            }
            isDownDirection = false;
            currentStationId = addUpStationIdAndGetNextKey(list, currentStationId);
        }
        return list;
    }

    private Long addDownStationIdAndGetNextKey(List<Station> list, Long upStationId) {
        Long downStationId = sectionMap.getDownStationIdOf(upStationId);
        list.add(entityMap.findEntityOfId(downStationId));
        return downStationId;
    }

    private Long addUpStationIdAndGetNextKey(List<Station> list, Long downStationId) {
        Long upStationId = sectionMap.getUpStationIdOf(downStationId);
        list.add(0, entityMap.findEntityOfId(upStationId));
        return upStationId;
    }
}
