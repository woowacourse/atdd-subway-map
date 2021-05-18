package wooteco.subway.line.domain.section;

import wooteco.subway.line.domain.section.validate.add.SectionAddValidator;
import wooteco.subway.line.domain.section.validate.add.SectionAddValidatorImpl;
import wooteco.subway.line.domain.section.validate.delete.SectionDeleteValidator;
import wooteco.subway.line.domain.section.validate.delete.SectionDeleteValidatorImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private static final Sections EMPTY = new Sections(Collections.emptyList());

    private static final int END_POINT_COUNT = 1;

    private final List<Section> sections;
    private final SectionAddValidator addValidator;
    private final SectionDeleteValidator deleteValidator;

    public Sections(List<Section> sections) {
        this.sections = sections;
        this.addValidator = new SectionAddValidatorImpl();
        this.deleteValidator = new SectionDeleteValidatorImpl();
    }

    public static Sections empty() {
        return EMPTY;
    }

    public void add(Section section) {
        addValidator.validatePossibleToAdd(this, section);
        if (isAddToEndPoint(section)) {
            sections.add(section);
            return;
        }
        addToBetween(section);
    }

    private void addToBetween(Section newSection) {
        Section section = findSectionToConnect(newSection);
        section.validateAddableDistance(newSection);
        sections.remove(section);
        sections.add(newSection);
        addUpStation(section, newSection);
        addDownStation(section, newSection);
    }

    private void addUpStation(Section section, Section newSection) {
        if (section.isSameDownStationId(newSection)) {
            sections.add(new Section(newSection.getLineId(), section.getUpStationId(),
                    newSection.getUpStationId(), section.distanceDifference(newSection)));
        }
    }

    private void addDownStation(Section section, Section newSection) {
        if (section.isSameUpStationId(newSection)) {
            sections.add(new Section(newSection.getLineId(), newSection.getDownStationId(),
                    section.getDownStationId(), section.distanceDifference(newSection)));
        }
    }

    private Section findSectionToConnect(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasUpStationIdOrDownStationId(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("연결할 역을 찾지 못했습니다."));
    }

    private boolean isAddToEndPoint(Section newSection) {
        boolean notExistUpStationId = sections.stream()
                .noneMatch(section -> section.isSameUpStationId(newSection));
        boolean notExistDownStationId = sections.stream()
                .noneMatch(section -> section.isSameDownStationId(newSection));
        return notExistUpStationId && notExistDownStationId;
    }

    public void delete(Long stationId) {
        deleteValidator.validateDeleteSection(this, stationId);
        if (isDeleteToEndPoint(stationId)) {
            deleteEndPoint(stationId);
            return;
        }
        deleteSectionOfStation(stationId);
    }

    private void deleteSectionOfStation(Long stationId) {
        List<Section> findSections = sections.stream()
                .filter(section -> section.hasStationId(stationId))
                .collect(Collectors.toList());

        sections.add(findSections.get(0).mergeWithoutDuplicateStationId(findSections.get(1)));
        findSections.forEach(sections::remove);
    }

    private void deleteEndPoint(Long stationId) {
        Section findSection = sections.stream()
                .filter(section -> section.hasStationId(stationId))
                .findAny().orElseThrow(() -> new IllegalArgumentException("삭제할 역을 가진 구간이 존재하지 않습니다."));
        sections.remove(findSection);
    }

    private boolean isDeleteToEndPoint(Long stationId) {
        int count = (int) sections.stream()
                .filter(section -> section.hasStationId(stationId))
                .count();
        return count == END_POINT_COUNT;
    }

    public boolean isExistStationId(Long stationId) {
        return this.stationIds().contains(stationId);
    }

    public List<Long> stationIds() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
                .distinct()
                .collect(Collectors.toList());
    }

    private Sections sortSection() {
        Deque<Section> sectionDeque = new ArrayDeque<>();
        sectionDeque.add(sections.get(0));

        Map<Long, Section> upStationIdMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
        while (upStationIdMap.containsKey(sectionDeque.peekLast().getDownStationId())) {
            sectionDeque.addLast(upStationIdMap.get(sectionDeque.peekLast().getDownStationId()));
        }

        Map<Long, Section> downStationIdMap = sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));
        while (downStationIdMap.containsKey(sectionDeque.peekFirst().getUpStationId())) {
            sectionDeque.addFirst(downStationIdMap.get(sectionDeque.peekFirst().getUpStationId()));
        }
        return new Sections(new ArrayList<>(sectionDeque));
    }

    public List<Long> sortSectionsId() {
        return sortSection().stationIds();
    }

    public List<Section> toList() {
        return new ArrayList<>(sections);
    }
}
