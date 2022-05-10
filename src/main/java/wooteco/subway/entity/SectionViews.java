package wooteco.subway.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SectionViews {

    private final Map<Long, StationEntity> fromUpStationIdMap;
    private final Map<Long, StationEntity> fromDownStationIdMap;

    private SectionViews(Map<Long, StationEntity> fromUpStationIdMap,
                         Map<Long, StationEntity> fromDownStationIdMap) {
        this.fromUpStationIdMap = fromUpStationIdMap;
        this.fromDownStationIdMap = fromDownStationIdMap;
    }

    public static SectionViews of(List<SectionViewEntity> sectionViewEntities) {
        Map<Long, StationEntity> fromUpStationIdMap = new HashMap<>();
        Map<Long, StationEntity> fromDownStationIdMap = new HashMap<>();

        for (SectionViewEntity sectionView : sectionViewEntities) {
            Long upStationId = sectionView.getUpStationId();
            Long downStationId = sectionView.getDownStationId();

            fromUpStationIdMap.put(upStationId, sectionView.getDownStation());
            fromDownStationIdMap.put(downStationId, sectionView.getUpStation());
        }

        return new SectionViews(fromUpStationIdMap, fromDownStationIdMap);
    }

    public List<StationEntity> getSortedStationsList() {
        StationEntity initialStation = getInitialStation();
        boolean shouldAppendRight = true;
        int stationEntityCount = fromUpStationIdMap.values().size() + 1;

        return toSortedStationList(initialStation, shouldAppendRight, stationEntityCount);
    }

    private List<StationEntity> toSortedStationList(StationEntity initialStation,
                                                    boolean shouldAppendRight,
                                                    int capacity) {
        LinkedList<StationEntity> list = new LinkedList<>(List.of(initialStation));
        Long key = initialStation.getId();
        while (list.size() < capacity) {
            if (shouldAppendRight && fromUpStationIdMap.containsKey(key)) {
                key = appendRightAndGetNextKey(list, key);
                continue;
            }
            shouldAppendRight = false;
            key = appendLeftAndGetNextKey(list, key);
        }
        return list;
    }

    private StationEntity getInitialStation() {
        Long initialKey = (Long) fromUpStationIdMap.keySet().toArray()[0];
        return fromDownStationIdMap.values()
                .stream()
                .filter(station -> station.hasIdOf(initialKey))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 로직이 잘못 구현되었습니다."));
    }

    private Long appendRightAndGetNextKey(List<StationEntity> list, Long key) {
        StationEntity value = fromUpStationIdMap.get(key);
        list.add(value);
        return value.getId();
    }

    private Long appendLeftAndGetNextKey(List<StationEntity> list, Long key) {
        StationEntity value = fromDownStationIdMap.get(key);
        list.add(0, value);
        return value.getId();
    }
}
