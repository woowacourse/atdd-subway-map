package wooteco.subway.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    public static final int FIRST_SECTION_INDEX = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validateSection(long upStationId, long downStationId, int distance) {
        validateBothStationExist(upStationId, downStationId);
        validateNoneStationExist(upStationId, downStationId);
        validateDistance(upStationId, downStationId, distance);
    }

    private void validateBothStationExist(long upStationId, long downStationId) {
        if (existByStationId(upStationId)
            && existByStationId(downStationId)) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재합니다.");
        }
    }

    private boolean existByStationId(long stationId) {
        return existByUpStationId(stationId) || existByDownStationId(stationId);
    }

    private boolean existByUpStationId(long stationId) {
        return sections.stream()
            .map(Section::getUpStationId)
            .anyMatch(id -> id == stationId);
    }

    private boolean existByDownStationId(long stationId) {
        return sections.stream()
            .map(Section::getDownStationId)
            .anyMatch(id -> id == stationId);
    }

    private void validateNoneStationExist(long upStationId, long downStationId) {
        if (!existByStationId(upStationId)
            && !existByStationId(downStationId)) {
            throw new IllegalArgumentException("상행, 하행이 대상 노선에 둘 다 존재하지 않습니다.");
        }
    }

    private void validateDistance(long upStationId, long downStationId, int distance) {
        if (isInvalidDistanceWithDownStationOverlap(downStationId, distance)
            || isInvalidDistanceWithUpStationOverlap(upStationId, distance)) {
            throw new IllegalArgumentException(
                "역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }

    private boolean isInvalidDistanceWithDownStationOverlap(long downStationId, int distance) {
        return existByDownStationId(downStationId)
            && findDistanceById(findIdByDownStationId(downStationId)) <= distance;
    }

    private boolean isInvalidDistanceWithUpStationOverlap(long upStationId, int distance) {
        return existByUpStationId(upStationId)
            && findDistanceById(findIdByUpStationId(upStationId)) <= distance;
    }

    private long findIdByUpStationId(long stationId) {
        return sections.stream()
            .filter(section -> section.isSameUpStationId(stationId))
            .map(Section::getId)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("일치하는 Section이 존재하지 않습니다."));
    }

    private long findIdByDownStationId(long stationId) {
        return sections.stream()
            .filter(section -> section.isSameDownStationId(stationId))
            .map(Section::getId)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("일치하는 Section이 존재하지 않습니다."));
    }

    private int findDistanceById(Long id) {
        return sections.stream()
            .filter(section -> section.isSameId(id))
            .findAny()
            .map(Section::getDistance)
            .orElseThrow(() -> new IllegalArgumentException("일치하는 Section이 존재하지 않습니다."));
    }

    public List<Section> findOverlapSection(long upStationId, long downStationId, int distance) {
        if (existByUpStationId(upStationId)) {
            Section section = findById(findIdByUpStationId(upStationId));
            return separateSectionInExistUpMatchCase(upStationId, downStationId, distance, section);
        }
        if (existByDownStationId(downStationId)) {
            Section section = findById(findIdByDownStationId(downStationId));
            return separateSectionInExistDownMatchCase(upStationId, downStationId, distance,
                section);
        }
        if (existByUpStationId(downStationId)) {
            Section section = findById(findIdByUpStationId(downStationId));
            return separateSectionInExistUpMatchCase(upStationId, downStationId, distance, section);
        }
        Section section = findById(findIdByDownStationId(upStationId));
        return separateSectionInExistDownMatchCase(upStationId, downStationId, distance, section);
    }

    private List<Section> separateSectionInExistUpMatchCase(
        long upStationId, long downStationId, int distance, Section section) {
        return List.of(
            Section.createOf(section.getId(), section.getLineId(), upStationId, downStationId,
                section.getDistance() - distance, section.getLineOrder()),
            Section.createOf(section.getId(), section.getLineId(), downStationId,
                section.getDownStationId(),
                section.getDistance() - distance, section.getLineOrder() + 1));
    }

    private List<Section> separateSectionInExistDownMatchCase(
        long upStationId, long downStationId, int distance, Section section) {
        return List.of(
            Section.createOf(section.getId(), section.getLineId(), section.getUpStationId(),
                upStationId,
                section.getDistance() - distance, section.getLineOrder()),
            Section.createOf(section.getId(), section.getLineId(), upStationId, downStationId,
                section.getDistance() - distance, section.getLineOrder() + 1));
    }

    private Section findById(long sectionId) {
        return sections.stream()
            .filter(section -> section.isSameId(sectionId))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("일치하는 Section이 존재하지 않습니다."));
    }

    public List<Long> getStationsId() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Section> sortedSections = createSortedSections();
        List<Long> stationIds = upStationIdsOf(sortedSections);
        stationIds.add(sortedSections.get(sortedSections.size() - 1).getDownStationId());

        return stationIds;
    }

    private List<Long> upStationIdsOf(List<Section> sections) {
        return sections.stream()
            .map(Section::getUpStationId)
            .collect(Collectors.toList());
    }

    private List<Section> createSortedSections() {
        return sections.stream()
            .sorted(Comparator.comparingLong(Section::getLineOrder))
            .collect(Collectors.toList());
    }

    public boolean hasTwoSection() {
        return sections.size() == 2;
    }

    public Section getSingleDeleteSection() {
        return sections.get(FIRST_SECTION_INDEX);
    }

    public Section getUpsideSection() {
        return createSortedSections().get(FIRST_SECTION_INDEX);
    }

    public Section getDownsideSection() {
        List<Section> sortedSections = createSortedSections();
        return sortedSections.get(sortedSections.size() - 1);
    }
}
