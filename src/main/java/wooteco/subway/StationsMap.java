package wooteco.subway;

import wooteco.subway.section.domain.Section;

import java.util.*;

public class StationsMap {
    Map<Long, Section> upToDownStationMap;
    Map<Long, Section> downToUpStationMap;
    Deque<Long> upToDownSerializedMap;


    private StationsMap(Map<Long, Section> upToDownStationMap, Map<Long, Section> downToUpStationMap,
                  Deque<Long> upToDownSerializedMap) {
        this.upToDownStationMap = upToDownStationMap;
        this.downToUpStationMap = downToUpStationMap;
        this.upToDownSerializedMap = upToDownSerializedMap;
    }

    public static StationsMap from(List<Section> sectionsByLineId){
        Map<Long, Section> upToDownStationMap = new HashMap<>();
        Map<Long, Section> downToUpStationMap = new HashMap<>();
        Deque<Long> upToDownSerializedMap = new ArrayDeque<>();
        for (Section section : sectionsByLineId) {
            upToDownStationMap.put(section.getUpStationId(), section);
            downToUpStationMap.put(section.getDownStationId(), section);
        }

        Long downStationId = sectionsByLineId.get(0).getDownStationId();
        Long upStationId = sectionsByLineId.get(0).getUpStationId();
        upToDownSerializedMap.add(downStationId);

        do {
            if (downToUpStationMap.containsKey(downStationId)) {
                Section nextSection = downToUpStationMap.get(downStationId);
                upToDownSerializedMap.addFirst(nextSection.getUpStationId());
                downStationId = nextSection.getUpStationId();
            }
            if (upToDownStationMap.containsKey(upStationId)) {
                Section nextSection = upToDownStationMap.get(upStationId);
                upToDownSerializedMap.addLast(nextSection.getDownStationId());
                upStationId = nextSection.getDownStationId();
            }
        } while (downToUpStationMap.containsKey(downStationId) || upToDownStationMap.containsKey(upStationId));
        return new StationsMap(upToDownStationMap, downToUpStationMap, upToDownSerializedMap);
    }

    public boolean isDownStation(Section section) {
        return upToDownSerializedMap.getLast().equals(section.getUpStationId());
    }

    public boolean isUpStation(Section section) {
        return upToDownSerializedMap.getFirst().equals(section.getDownStationId());
    }
}
