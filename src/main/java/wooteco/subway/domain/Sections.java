package wooteco.subway.domain;

import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.section.IllegalMergeSectionException;
import wooteco.subway.exception.section.NoSuchSectionException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private static final int MERGE_SECTION_SIZE = 2;
    private static final int ONE_SECTION = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Section getSectionToUpdate(Section sectionToDelete, Section sectionToInsert) {
        return sectionToDelete.createSection(sectionToInsert);
    }

    public Optional<Section> getSectionToDelete(Section section) {
        if (canAddAsLastStation(section)) {
            return Optional.empty();
        }
        return Optional.of(getModifiableSectionAfterInsert(section));
    }

    private boolean canAddAsLastStation(Section section) {
        List<Long> upStationIds = getUpStationIds();
        List<Long> downStationIds = getDownStationIds();

        Long upLastStationId = getUpLastStationId(upStationIds, downStationIds);
        Long downLastStationId = getDownLastStationId(upStationIds, downStationIds);

        return section.canAddAsLastStation(upLastStationId, downLastStationId);
    }

    private Section getModifiableSectionAfterInsert(Section insertableSection) {
        Optional<Section> upStationSection = getExistingUpStationSection(insertableSection);
        Optional<Section> downStationSection = getExistingDownStationSection(insertableSection);

        Section existingSection = upStationSection.orElseGet(() -> downStationSection.orElseThrow(NoSuchSectionException::new));

        validateDistance(existingSection, insertableSection);

        return existingSection;
    }

    public List<Long> getDistinctStationIds() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStationId(), section.getDownStationId()))
                .flatMap(Collection::stream)
                .distinct()
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
            throw new SubwayException("구간이 하나인 경우에는 삭제할 수 없습니다.");
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

    public void validateInsertable(Section section) {
        if (isUnableToAdd(section)) {
            throw new SubwayException("추가할 수 없는 구간입니다.");
        }
    }

    private int calculateMergedDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    private void validateDistance(Section existingSection, Section insertableSection) {
        if (insertableSection.isDistanceBiggerThan(existingSection)) {
            throw new SubwayException("불가능한 구간의 길이입니다.");
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

    private Long getUpLastStationId(List<Long> upStationIds, List<Long> downStationIds) {
        List<Long> upIds = new ArrayList<>(upStationIds);
        upIds.removeAll(downStationIds);

        //이거 검증해줘야할까?
        return upIds.get(0);
    }

    private Long getDownLastStationId(List<Long> upStationIds, List<Long> downStationIds) {
        List<Long> downIds = new ArrayList<>(downStationIds);
        downIds.removeAll(upStationIds);

        //이거 검증해줘야할까?
        return downIds.get(0);
    }

    private boolean isUnableToAdd(Section checkableSection) {
        List<Long> distinctStationIds = getDistinctStationIds();

        return distinctStationIds.contains(checkableSection.getUpStationId()) == distinctStationIds.contains(checkableSection.getDownStationId());
    }

    public List<Section> getSections() {
        return sections;
    }
}
