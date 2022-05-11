package wooteco.subway.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionMap;
import wooteco.subway.domain.StationEntityMap;

public class SectionViews {

    private final SectionMap sectionMap;
    private final StationEntityMap entityMap;

    private SectionViews(SectionMap sectionMap,
                         StationEntityMap entityMap) {
        this.sectionMap = sectionMap;
        this.entityMap = entityMap;
    }

    public static SectionViews of(List<SectionViewEntity> sectionViewEntities) {
        return new SectionViews(
                SectionMap.of(toSections(sectionViewEntities)),
                StationEntityMap.of(sectionViewEntities));
    }

    private static List<Section> toSections(List<SectionViewEntity> sectionViewEntities) {
        return sectionViewEntities.stream()
                .map(SectionViewEntity::toSection)
                .collect(Collectors.toList());
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
