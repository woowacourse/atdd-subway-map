package wooteco.subway.line.domain.section;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private static final Sections EMPTY = new Sections(Collections.emptyList());

    private static final int END_POINT_COUNT = 1;
    private static final int DELETE_LIMIT_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections empty() {
        return EMPTY;
    }

    public void add(Section section) {
        validatePossibleToAdd(this, section);
        if (isAddToEndPoint(section)) {
            sections.add(section);
            return;
        }
        addToBetween(section);
    }

    private void validatePossibleToAdd(Sections sections, Section newSection) {
        boolean existUpStation = sections.isExistStationId(newSection.getUpStationId());
        boolean existDownStation = sections.isExistStationId(newSection.getDownStationId());
        validateNotExistSectionOfStation(existUpStation, existDownStation);
        validateAlreadyExistSectionOfStation(existUpStation, existDownStation);
    }

    private void validateNotExistSectionOfStation(boolean existUpStation, boolean existDownStation) {
        if (!existUpStation && !existDownStation) {
            throw new IllegalArgumentException("연결할 수 있는 역이 구간내에 없습니다.");
        }
    }

    private void validateAlreadyExistSectionOfStation(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 존재합니다.");
        }
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
        validateDeleteSection(this, stationId);
        if (isDeleteToEndPoint(stationId)) {
            deleteEndPoint(stationId);
            return;
        }
        deleteSectionOfStation(stationId);
    }

    private void validateDeleteSection(Sections sections, Long stationId) {
        validateExistStationId(sections, stationId);
        validateDeleteSize(sections);
    }

    private void validateExistStationId(Sections sections, Long stationId) {
        if (sections.isExistStationId(stationId)) {
            return;
        }
        throw new IllegalArgumentException("삭제하려는 역을 포함하는 구간이 존재하지 않습니다.");
    }

    private void validateDeleteSize(Sections sections) {
        if (sections.toList().size() <= DELETE_LIMIT_SIZE) {
            throw new IllegalStateException("구간이 하나 이하일 때는 삭제할 수 없습니다.");
        }
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

    private List<Long> stationIds() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
                .distinct()
                .collect(Collectors.toList());
    }

    private Sections sortSection() {
        Deque<Section> sectionDeque = new ArrayDeque<>();
        sectionDeque.add(sections.get(0));
        sortUpSections(sectionDeque);
        sortDownSections(sectionDeque);
        return new Sections(new ArrayList<>(sectionDeque));
    }

    private void sortUpSections(Deque<Section> sectionDeque) {
        Map<Long, Section> upStationIdMap = this.sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
        while (upStationIdMap.containsKey(sectionDeque.peekLast().getDownStationId())) {
            sectionDeque.addLast(upStationIdMap.get(sectionDeque.peekLast().getDownStationId()));
        }
    }

    private void sortDownSections(Deque<Section> sectionDeque) {
        Map<Long, Section> downStationIdMap = this.sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));
        while (downStationIdMap.containsKey(sectionDeque.peekFirst().getUpStationId())) {
            sectionDeque.addFirst(downStationIdMap.get(sectionDeque.peekFirst().getUpStationId()));
        }
    }

    public List<Long> sortSectionsId() {
        return sortSection().stationIds();
    }

    public List<Section> toList() {
        return new ArrayList<>(sections);
    }
}
