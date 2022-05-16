package wooteco.subway.domain;

import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.section.IllegalMergeSectionException;
import wooteco.subway.exception.section.NoSuchSectionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private static final int MERGE_SECTION_SIZE = 2;
    private static final int ONE_SECTION = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections addSection(Section sectionToInsert) {
        validateInsertable(sectionToInsert);

        Optional<Section> deletableSection = findSectionToDelete(sectionToInsert);

        sections.add(sectionToInsert);
        deletableSection.ifPresent(sectionToDelete -> {
            Section sectionToUpdate = getSectionToUpdate(sectionToDelete, sectionToInsert);
            sections.remove(sectionToDelete);
            sections.add(sectionToUpdate);
        });


        return new Sections(sections);
    }

    public Section getSectionToUpdate(Section sectionToDelete, Section sectionToInsert) {
        return sectionToDelete.createSection(sectionToInsert);
    }

    public Optional<Section> findSectionToDelete(Section section) {
        if (canAddAsLastStation(section)) {
            return Optional.empty();
        }
        return Optional.of(findModifiableSectionAfterInsert(section));
    }

    private boolean canAddAsLastStation(Section section) {
        List<Long> upStationIds = findUpStationIds();
        List<Long> downStationIds = findDownStationIds();

        Long upLastStationId = findUpLastStationId(upStationIds, downStationIds);
        Long downLastStationId = findDownLastStationId(upStationIds, downStationIds);

        return section.canAddAsLastStation(upLastStationId, downLastStationId);
    }

    private Section findModifiableSectionAfterInsert(Section insertableSection) {
        Optional<Section> upStationSection = findExistingUpStationSection(insertableSection);
        Optional<Section> downStationSection = findExistingDownStationSection(insertableSection);

        Section existingSection = upStationSection.orElseGet(() -> downStationSection.orElseThrow(NoSuchSectionException::new));

        validateDistance(existingSection, insertableSection);

        return existingSection;
    }

    public Set<Long> findDistinctStationIds() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
                .collect(Collectors.toSet());
    }

    public Optional<Section> findExistingUpStationSection(Section upStationSection) {
        return sections.stream()
                .filter(section -> section.isUpStationIdEquals(upStationSection))
                .findFirst();
    }

    public Optional<Section> findExistingDownStationSection(Section downStationSection) {
        return sections.stream()
                .filter(section -> section.isDownStationIdEquals(downStationSection))
                .findFirst();
    }

    public Sections findByStationId(Long stationId) {
        return new Sections(sections.stream()
                .filter(section -> section.isEqualDownStationId(stationId) || section.isEqualUpStationId(stationId))
                .collect(Collectors.toList()));
    }

    public boolean needMerge() {
        return sections.size() == MERGE_SECTION_SIZE;
    }

    public void validateDeletable() {
        if (sections.size() == ONE_SECTION) {
            throw new SubwayException("구간이 하나인 경우에는 삭제할 수 없습니다.");
        }
    }

    public Section mergeSections() {
        validateMergeSections();
        int distance = calculateMergedDistance();
        List<Long> mergedIds = findMergedStationIds();

        return new Section(distance, sections.get(0).getLineId(), mergedIds.get(0), mergedIds.get(1));
    }

    private void validateMergeSections() {
        if (sections.size() != MERGE_SECTION_SIZE) {
            throw new IllegalMergeSectionException();
        }
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

    private List<Long> findUpStationIds() {
        return sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
    }

    private List<Long> findDownStationIds() {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    private List<Long> findMergedStationIds() {
        Section section1 = sections.get(0);
        Section section2 = sections.get(1);

        if (section1.isEqualUpStationId(section2.getDownStationId())) {
            return List.of(section2.getUpStationId(), section1.getDownStationId());
        }
        return List.of(section1.getUpStationId(), section2.getDownStationId());
    }

    private Long findUpLastStationId(List<Long> upStationIds, List<Long> downStationIds) {
        List<Long> upIds = new ArrayList<>(upStationIds);
        upIds.removeAll(downStationIds);

        return upIds.get(0);
    }

    private Long findDownLastStationId(List<Long> upStationIds, List<Long> downStationIds) {
        List<Long> downIds = new ArrayList<>(downStationIds);
        downIds.removeAll(upStationIds);

        return downIds.get(0);
    }

    private boolean isUnableToAdd(Section checkableSection) {
        Set<Long> distinctStationIds = findDistinctStationIds();

        return distinctStationIds.contains(checkableSection.getUpStationId()) == distinctStationIds.contains(checkableSection.getDownStationId());
    }

    public List<Section> getSections() {
        return sections;
    }

    public Sections deleteSection(Long stationId) {
        validateDeletable();
        Sections sectionsToDelete = findByStationId(stationId);

        sections.removeAll(sectionsToDelete.getSections());

        if (sectionsToDelete.needMerge()) {
            Section section = sectionsToDelete.mergeSections();
            sections.add(section);
        }

        return new Sections(sections);
    }
}
