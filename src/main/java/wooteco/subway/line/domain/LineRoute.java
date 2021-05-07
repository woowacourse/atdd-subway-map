package wooteco.subway.line.domain;

import wooteco.subway.section.domain.Section;

import java.util.*;

public class LineRoute {
    private final Map<Long, Section> upToDownStationMap = new HashMap<>();
    private final Map<Long, Section> downToUpStationMap = new HashMap<>();
    private final Deque<Long> upToDownSerializedMap = new ArrayDeque<>();

    public LineRoute(List<Section> sectionsByLineId) {
        createDirectedRoute(sectionsByLineId);
        serializeRoute(sectionsByLineId);
    }

    private void createDirectedRoute(List<Section> sectionsByLineId) {
        for (Section section : sectionsByLineId) {
            upToDownStationMap.put(section.getUpStationId(), section);
            downToUpStationMap.put(section.getDownStationId(), section);
        }
    }

    private void serializeRoute(List<Section> sectionsByLineId) {
        Long downStationId = sectionsByLineId.get(0).getDownStationId();
        Long upStationId = sectionsByLineId.get(0).getUpStationId();
        do {
            downStationId = expandDownToUp(downStationId);
            upStationId = expandUptoDown(upStationId);
        } while (downToUpStationMap.containsKey(downStationId) || upToDownStationMap.containsKey(upStationId));
    }

    private Long expandUptoDown(Long upStationId) {
        if (upToDownStationMap.containsKey(upStationId)) {
            Section nextSection = upToDownStationMap.get(upStationId);
            upToDownSerializedMap.addLast(nextSection.getDownStationId());
            upStationId = nextSection.getDownStationId();
        }
        return upStationId;
    }

    private Long expandDownToUp(Long downStationId) {
        if (downToUpStationMap.containsKey(downStationId)) {
            Section nextSection = downToUpStationMap.get(downStationId);
            upToDownSerializedMap.addFirst(nextSection.getUpStationId());
            downStationId = nextSection.getUpStationId();
        }
        return downStationId;
    }

    public boolean isEndOfLine(Long stationId) {
        return upToDownSerializedMap.getLast().equals(stationId) || upToDownSerializedMap.getFirst().equals(stationId);
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

    public Deque<Long> getOrderedStations() {
        return new ArrayDeque<>(upToDownSerializedMap);
    }

    public Set<Long> getStationIds() {
        return new HashSet<>(upToDownSerializedMap);
    }
}
