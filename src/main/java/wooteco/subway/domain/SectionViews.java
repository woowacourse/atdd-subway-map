package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.entity.SectionViewEntity;
import wooteco.subway.entity.StationEntity;

public class SectionViews {

    private final SectionStationMap sectionMap;
    private final StationEntityMap entityMap;

    private SectionViews(SectionStationMap sectionMap,
                         StationEntityMap entityMap) {
        this.sectionMap = sectionMap;
        this.entityMap = entityMap;
    }

    public static SectionViews of(List<SectionViewEntity> sectionViewEntities) {
        List<SectionStationInfo> sectionStationInfos = sectionViewEntities.stream()
                .map(SectionViewEntity::toSectionStationInfo)
                .collect(Collectors.toList());

        return new SectionViews(SectionStationMap.of(sectionStationInfos),
                StationEntityMap.of(sectionViewEntities));
    }

    public List<StationEntity> getSortedStationsList() {
        Long initialStationId = sectionMap.findAnyStationId();
        boolean isDownDirection = true;
        int stationEntityCount = entityMap.getSize();

        return toSortedStationList(initialStationId, isDownDirection, stationEntityCount);
    }

    private List<StationEntity> toSortedStationList(Long initialStationId,
                                                    boolean isDownDirection,
                                                    int capacity) {
        LinkedList<StationEntity> list = new LinkedList<>();
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

    private Long addDownStationIdAndGetNextKey(List<StationEntity> list, Long upStationId) {
        Long downStationId = sectionMap.getDownStationIdOf(upStationId);
        list.add(entityMap.findEntityOfId(downStationId));
        return downStationId;
    }

    private Long addUpStationIdAndGetNextKey(List<StationEntity> list, Long downStationId) {
        Long upStationId = sectionMap.getUpStationIdOf(downStationId);
        list.add(0, entityMap.findEntityOfId(upStationId));
        return upStationId;
    }
}
