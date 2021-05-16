package wooteco.subway.domain;

import wooteco.subway.exception.section.SectionAlreadyExistBothStationException;
import wooteco.subway.exception.section.SectionMiniMumDeleteException;
import wooteco.subway.exception.section.SectionNotExistBothStationException;
import wooteco.subway.exception.section.SectionNotExistException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    public static final int MINIMUM_SECTION_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validateAddableNewStation(Section section) {
        Deque<Long> stationIdsInOrder = getSortedStationIds();
        if (stationIdsInOrder.contains(section.getUpStationId()) && stationIdsInOrder.contains(section.getDownStationId())) {
            throw new SectionAlreadyExistBothStationException(section);
        }
        if (!stationIdsInOrder.contains(section.getUpStationId()) && !stationIdsInOrder.contains(section.getDownStationId())) {
            throw new SectionNotExistBothStationException(section);
        }
    }

    public void validateSectionSize(Long lineId) {
        if (getSectionsSize() == MINIMUM_SECTION_SIZE) {
            throw new SectionMiniMumDeleteException(lineId);
        }
    }

    public Deque<Long> getSortedStationIds() {
        Deque<Long> sortedStationIds = new ArrayDeque<>();
        Map<Long, Long> upStationIds = new LinkedHashMap<>();
        Map<Long, Long> downStationIds = new LinkedHashMap<>();

        initSortedStationId(sortedStationIds, upStationIds, downStationIds);
        sortPreviousStationIds(sortedStationIds, upStationIds);
        sortFollowingStationIds(sortedStationIds, downStationIds);

        return sortedStationIds;
    }

    private void initSortedStationId(Deque<Long> sortedStationIds, Map<Long, Long> upStationIds, Map<Long, Long> downStationIds) {
        for (Section section : sections) {
            upStationIds.put(section.getDownStationId(), section.getUpStationId());
            downStationIds.put(section.getUpStationId(), section.getDownStationId());
        }
        Section now = sections.get(0);
        sortedStationIds.addFirst(now.getUpStationId());
    }

    private void sortPreviousStationIds(Deque<Long> sortedStationIds, Map<Long, Long> upStationIds) {
        while (upStationIds.containsKey(sortedStationIds.peekFirst())) {
            Long currentId = sortedStationIds.peekFirst();
            sortedStationIds.addFirst(upStationIds.get(currentId));
        }
    }

    private void sortFollowingStationIds(Deque<Long> sortedStationIds, Map<Long, Long> downStationIds) {
        while (downStationIds.containsKey(sortedStationIds.peekLast())) {
            Long currentId = sortedStationIds.peekLast();
            sortedStationIds.addLast(downStationIds.get(currentId));
        }
    }

    private List<Long> getUpStationIds() {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    private List<Long> getDownStationIds() {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public Section getPreviousSection(Section newSection) {
        return sections.stream()
                .filter(section -> newSection.getUpStationId().equals(section.getUpStationId()))
                .findAny()
                .orElseThrow(SectionNotExistException::new);
    }

    public Section getFollowingSection(Section newSection) {
        return sections.stream()
                .filter(section -> newSection.getDownStationId().equals(section.getDownStationId()))
                .findAny()
                .orElseThrow(SectionNotExistException::new);
    }

    public boolean isExistInUpStationIds(Long stationId) {
        return getUpStationIds().contains(stationId);
    }

    public boolean isExistInDownStationIds(Long stationId) {
        return getDownStationIds().contains(stationId);
    }

    public boolean isFirstOrLastStation(Long stationIdToDelete) {
        return getFirstStationId().equals(stationIdToDelete)
                || getLastStationId().equals(stationIdToDelete);
    }

    private Long getFirstStationId() {
        return getUpStationIds().stream()
                .filter(upId -> getDownStationIds().stream()
                        .noneMatch(downId -> downId.equals(upId)))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("상행 종점이 없습니다."));
    }

    private Long getLastStationId() {
        return getDownStationIds().stream()
                .filter(downId -> getUpStationIds().stream()
                        .noneMatch(upId -> upId.equals(downId)))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("하행 종점이 없습니다."));
    }

    public int getSectionsSize() {
        return sections.size();
    }

    public Long getSectionIdToDelete(Long stationIdToDelete) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationIdToDelete)
                        || section.getDownStationId().equals(stationIdToDelete))
                .map(section -> section.getId())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 역 : " + stationIdToDelete + "에 해당하는 구간이 없습니다."));
    }

    public Long getUpStationSectionId(Long stationIdToDelete) {
        Section UpStationSection = getUpStationSection(stationIdToDelete);
        return UpStationSection.getId();
    }

    private Section getUpStationSection(Long stationIdToDelete) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationIdToDelete))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(stationIdToDelete + "역 삭제 에러"));
    }

    public Long getDownStationSectionId(Long stationIdToDelete) {
        Section DownStationSection = getDownStationSection(stationIdToDelete);
        return DownStationSection.getId();
    }

    private Section getDownStationSection(Long stationIdToDelete) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationIdToDelete))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(stationIdToDelete + "역 삭제 에러"));
    }

    public Section getNewSection(Long lineId, Long stationIdToDelete) {
        Section upStationSection = getUpStationSection(stationIdToDelete);
        Section downStationSection = getDownStationSection(stationIdToDelete);

        int newDistance = upStationSection.getDistance() + downStationSection.getDistance();
        return new Section(lineId, upStationSection.getUpStationId(), downStationSection.getDownStationId(), newDistance);
    }
}
