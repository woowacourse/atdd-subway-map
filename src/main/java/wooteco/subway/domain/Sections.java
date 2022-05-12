package wooteco.subway.domain;

import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.section.IllegalMergeSectionException;
import wooteco.subway.exception.section.NoSuchSectionException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private static final int MERGE_SECTION_SIZE = 2;
    private static final int LAST_STATION_SIZE = 1;
    private static final int ONE_SECTION = 1;
    public static final int NO_STATION_MATCH = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getDistinctStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Long> getLastStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(it -> it))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() == LAST_STATION_SIZE)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Optional<Section> getExistingUpStationSection(Section upStationSection) {
        return sections.stream()
                .filter(section -> section.isUpStationIdEquals(upStationSection))
                .findFirst();
    }

    public Optional<Section> getExistingDownStationSection(Section downStationSection) {
        return sections.stream()
                .filter(section -> section.isDownStationIdEquals(downStationSection))
                .findFirst();
    }

    public boolean isLastStation(Long stationId) {
        return getLastStationIds().contains(stationId);
    }

    public Sections getByStationId(Long stationId) {
        return new Sections(sections.stream()
                .filter(section -> section.isEqualDownStationId(stationId) || section.isEqualUpStationId(stationId))
                .collect(Collectors.toList()));
    }

    public boolean isIntermediateStation() {
        return sections.size() == MERGE_SECTION_SIZE;
    }

    public void validateSize() {
        if (sections.size() == ONE_SECTION) {
            throw new IllegalArgumentException("구간이 하나인 경우에는 삭제할 수 없습니다.");
        }
    }

    public Section mergeSections() {
        if (sections.size() != MERGE_SECTION_SIZE) {
            throw new IllegalMergeSectionException();
        }

        int distance = calculateMergedDistance();
        List<Long> mergedIds = getMergedStationIds();

        return new Section(distance, sections.get(0).getLineId(), mergedIds.get(0), mergedIds.get(1));
    }

    private boolean isEqualDownStationId(Long downStationId, Section section) {
        return section.getDownStationId().equals(downStationId);
    }

    private List<Long> getMergedStationIds() {
        Section section1 = sections.get(0);
        Section section2 = sections.get(1);

        if (section1.isEqualUpStationId(section2.getDownStationId())) {
            return List.of(section2.getUpStationId(), section1.getDownStationId());
        }
        if (section2.isEqualUpStationId(section1.getDownStationId())) {
            return List.of(section1.getUpStationId(), section2.getDownStationId());
        }
        throw new IllegalMergeSectionException();
    }

    private int calculateMergedDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public List<Section> getSections() {
        return sections;
    }

    public Optional<Section> getSectionToDelete(Section section) {
        // 종점에 추가하는 경우 -> 아무것도 반환하지 않음
        if (canAddAsLastStation(section)) {
            return Optional.empty();
        }
        // 갈래길로 추가하는 경우 -> 수정할 갈래길을 반환하기
        return Optional.of(getModifiableSectionAfterInsert(section));
    }

    private Section getModifiableSectionAfterInsert(Section insertableSection) {
        Optional<Section> upStationSection = getExistingUpStationSection(insertableSection);
        Optional<Section> downStationSection = getExistingDownStationSection(insertableSection);

        Section existingSection = upStationSection.orElseGet(() -> downStationSection.orElseThrow(NoSuchSectionException::new));

        validateDistance(existingSection, insertableSection);

        return existingSection;
    }

    //이건 Section의 책임일듯
    private void validateDistance(Section existingSection, Section insertableSection) {
        if (insertableSection.getDistance() > existingSection.getDistance()) {
            throw new SubwayException("구간의 길이가 불가능합니다.");
        }
    }

    private boolean canAddAsLastStation(Section section) {
        List<Long> lastStationIds = getLastStationIds();

        return lastStationIds.contains(section.getUpStationId())
                || lastStationIds.contains(section.getDownStationId());
    }

    public void validateInsertable(Section section) {
        if (isUnableToAdd(section)) {
            throw new SubwayException("추가할 수 없는 구간입니다.");
        }
    }

    public Section getSectionToUpdate(Section sectionToDelete, Section sectionToInsert) {
        return sectionToDelete.createSection(sectionToInsert);
    }

    private boolean isUnableToAdd(Section checkableSection) {
        List<Long> distinctStationIds = getDistinctStationIds();

        return distinctStationIds.contains(checkableSection.getUpStationId()) == distinctStationIds.contains(checkableSection.getDownStationId());
    }
}
