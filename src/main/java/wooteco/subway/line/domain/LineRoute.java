package wooteco.subway.line.domain;

import wooteco.subway.section.domain.Section;

import java.util.*;

public class LineRoute {
    Map<Long, Section> upToDownStationMap;
    Map<Long, Section> downToUpStationMap;
    Deque<Long> upToDownSerializedMap;


    private LineRoute(Map<Long, Section> upToDownStationMap, Map<Long, Section> downToUpStationMap,
                      Deque<Long> upToDownSerializedMap) {
        this.upToDownStationMap = upToDownStationMap;
        this.downToUpStationMap = downToUpStationMap;
        this.upToDownSerializedMap = upToDownSerializedMap;
    }

    public static LineRoute from(List<Section> sectionsByLineId) {
        Map<Long, Section> upToDownStationMap = new HashMap<>();
        Map<Long, Section> downToUpStationMap = new HashMap<>();
        Deque<Long> upToDownSerializedMap = new ArrayDeque<>();
        for (Section section : sectionsByLineId) {
            upToDownStationMap.put(section.getUpStationId(), section);
            downToUpStationMap.put(section.getDownStationId(), section);
        }

        Long downStationId = sectionsByLineId.get(0).getDownStationId();
        Long upStationId = sectionsByLineId.get(0).getUpStationId();
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
        return new LineRoute(upToDownStationMap, downToUpStationMap, upToDownSerializedMap);
    }

    public boolean isEndOfDownStation(Long stationId) {
        return upToDownSerializedMap.getLast().equals(stationId);
    }

    public boolean isEndOfUpStation(Long stationId) {
        return upToDownSerializedMap.getFirst().equals(stationId);
    }

    public Deque<Long> getOrderedStations() {
        return upToDownSerializedMap;
    }

    public int getDistanceFromUpToDownStationMap(Long upStationId) {
        return upToDownStationMap.get(upStationId).getDistance();
    }

    public int getDistanceFromDownToUpStationMap(Long downStationId) {
        return downToUpStationMap.get(downStationId).getDistance();
    }

    public Section getSectionFromUpToDownStationMapByStationId(Long stationId) {
        return upToDownStationMap.get(stationId);
    }

    public Section getSectionFromDownToUpStationMapByStationId(Long stationId) {
        return downToUpStationMap.get(stationId);
    }
}
