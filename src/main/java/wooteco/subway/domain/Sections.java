package wooteco.subway.domain;

import wooteco.subway.exception.SubwayIllegalArgumentException;

import java.util.*;

public class Sections {
    private static final int INVALID_DELETABLE_STATION_COUNT = 2;

    private final Map<Long, Section> upToDownStationMap = new HashMap<>();
    private final Map<Long, Section> downToUpStationMap = new HashMap<>();
    private final Deque<Long> upToDownSerializedMap = new ArrayDeque<>();

    public Sections(List<Section> sectionsByLineId) {
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

    private boolean isEndOfUpLine(Long stationId) {
        return upToDownSerializedMap.getLast().equals(stationId);
    }

    private boolean isEndOfDownLine(Long stationId) {
        return upToDownSerializedMap.getFirst().equals(stationId);
    }

    public boolean isInsertSectionInEitherEndsOfLine(Section section) {
        return isEndOfUpLine(section.getUpStationId()) || isEndOfDownLine(section.getDownStationId());
    }

    public Section getSectionNeedToBeUpdatedForInsert(Section section) {
        if (upToDownStationMap.containsKey(section.getUpStationId())) {
            Section updateSection = upToDownStationMap.get(section.getUpStationId());
            return Section.of(updateSection.getId(),
                    updateSection.getLineId(),
                    section.getDownStationId(),
                    updateSection.getDownStationId(),
                    updateSection.getDistance() - section.getDistance());
        }

        Section updateSection = downToUpStationMap.get(section.getDownStationId());
        return Section.of(updateSection.getId(),
                updateSection.getLineId(),
                updateSection.getUpStationId(),
                section.getUpStationId(),
                updateSection.getDistance() - section.getDistance());
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

    public void validateInsert(Section section) {
        long count = upToDownSerializedMap.stream()
                .filter(sectionId -> sectionId.equals(section.getDownStationId()) || sectionId.equals(section.getUpStationId()))
                .count();

        if (count < 1) {
            throw new SubwayIllegalArgumentException("추가하려는 구간의 상행과 하행 중 한개의 역은 노선에 존재하여야 합니다.");
        }

        if (count > 1) {
            throw new SubwayIllegalArgumentException("추가하려는 구간의 상행과 하행 모두 노선에 존재합니다.");
        }
    }

    public void validateDeletable() {
        if (upToDownSerializedMap.size() == INVALID_DELETABLE_STATION_COUNT) {
            throw new SubwayIllegalArgumentException("구간이 하나인 노선에서 역은 더이상 삭제 할 수 없습니다.");
        }
    }
}
