package wooteco.subway.line.domain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import wooteco.subway.section.domain.Section;

public class LineRoute {
    private final Map<Long, Section> upToDownStationMap = new HashMap<>();
    private final Map<Long, Section> downToUpStationMap = new HashMap<>();
    private final Deque<Long> upToDownSerializedMap = new ArrayDeque<>();

    public LineRoute(List<Section> sectionsByLineId) {
        validateSectionsIsEmpty(sectionsByLineId);
        createDirectedRoute(sectionsByLineId);
        serializeRoute(sectionsByLineId);
    }

    private void validateSectionsIsEmpty(List<Section> sectionsByLineId) {
        if(sectionsByLineId.isEmpty()) {
            throw new IllegalArgumentException("구간이 등록되지 않은 정상적인 노선이 아닙니다.");
        }
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
        } while (downToUpStationMap.containsKey(downStationId) || upToDownStationMap
            .containsKey(upStationId));
    }

    private Long expandDownToUp(Long downStationId) {
        if (downToUpStationMap.containsKey(downStationId)) {
            Section nextSection = downToUpStationMap.get(downStationId);
            upToDownSerializedMap.addFirst(nextSection.getUpStationId());
            downStationId = nextSection.getUpStationId();
        }
        return downStationId;
    }

    private Long expandUptoDown(Long upStationId) {
        if (upToDownStationMap.containsKey(upStationId)) {
            Section nextSection = upToDownStationMap.get(upStationId);
            upToDownSerializedMap.addLast(nextSection.getDownStationId());
            upStationId = nextSection.getDownStationId();
        }
        return upStationId;
    }

    public boolean isInsertSectionInEitherEndsOfLine(Section section) {
        return isEndOfUpLine(section.getUpStationId()) || isEndOfDownLine(
            section.getDownStationId());
    }

    private boolean isEndOfUpLine(Long stationId) {
        return upToDownSerializedMap.getLast().equals(stationId);
    }

    private boolean isEndOfDownLine(Long stationId) {
        return upToDownSerializedMap.getFirst().equals(stationId);
    }

    public Section getSectionNeedToBeUpdatedForInsert(Section section) {
        if (upToDownStationMap.containsKey(section.getUpStationId())) {
            Section updateSection = upToDownStationMap.get(section.getUpStationId());
            return Section
                .of(updateSection.getId(), updateSection.getLineId(), section.getDownStationId(),
                    updateSection.getDownStationId(),
                    updateSection.getDistance() - section.getDistance());
        }

        Section updateSection = downToUpStationMap.get(section.getDownStationId());
        return Section
            .of(updateSection.getId(), updateSection.getLineId(), updateSection.getUpStationId(),
                section.getUpStationId(), updateSection.getDistance() - section.getDistance());
    }

    public Optional<Section> getSectionFromUpToDownStationMapByStationId(Long stationId) {
        return Optional.ofNullable(upToDownStationMap.get(stationId));
    }

    public Optional<Section> getSectionFromDownToUpStationMapByStationId(Long stationId) {
        return Optional.ofNullable(downToUpStationMap.get(stationId));
    }

    public Deque<Long> getOrderedStations() {
        return new ArrayDeque<>(upToDownSerializedMap);
    }

    public Set<Long> getStationIds() {
        return new HashSet<>(upToDownSerializedMap);
    }

    public int getStationsSize() {
        return upToDownSerializedMap.size();
    }
}
