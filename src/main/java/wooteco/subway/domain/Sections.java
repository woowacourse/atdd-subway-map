package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.exception.NoElementSectionsException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.exception.RemoveSectionException;
import wooteco.subway.exception.ResisterSectionException;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private final LinkedList<Section> value;

    public Sections(final LinkedList<Section> value) {
        validateSize(value);
        this.value = getSortedValue(value);
    }

    private void validateSize(final LinkedList<Section> value) {
        if (value.isEmpty()) {
            throw new NoElementSectionsException("[ERROR] 최소 한 개의 구간이 있어야 Sections 객체를 생성할 수 있습니다.");
        }
    }

    private LinkedList<Section> getSortedValue(final LinkedList<Section> sections) {
        final LinkedList<Section> sortedValue = new LinkedList<>();
        final Section firstSection = findFirstSection(sections);
        sortedValue.add(firstSection);
        for (int i = 0; i < sections.size() - 1; i++) {
            final Section nextSection = findNextSection(sections, sortedValue.getLast());
            sortedValue.add(nextSection);
        }
        return sortedValue;
    }

    private Section findFirstSection(final LinkedList<Section> sections) {
        return sections.stream()
                .filter(section -> isOnlyUpStation(sections, section.getUpStation()))
                .findAny()
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 해당 구간이 존재하지 않습니다."));
    }

    private boolean isOnlyUpStation(final LinkedList<Section> sections, final Station station) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(station))
                && sections.stream()
                .noneMatch(section -> section.getDownStation().equals(station));
    }

    private Section findNextSection(final LinkedList<Section> sections, final Section last) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(last.getDownStation()))
                .findAny()
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 해당 구간이 존재하지 않습니다."));
    }

    public Sections(Section first) {
        this.value = new LinkedList<>();
        value.add(first);
    }

    public List<Section> getValue() {
        return Collections.unmodifiableList(value);
    }

    public SectionsUpdateResult addSection(final Station newUpStation, final Station newDownStation,
                                           final Integer distance) {
        validateStationRegistration(newUpStation, newDownStation);

        if (isResistedStation(newUpStation)) {
            return addDownDirectionSection(newUpStation, newDownStation, distance);
        }
        return addUpDirectionSection(newUpStation, newDownStation, distance);
    }

    private void validateStationRegistration(final Station newUpStation, final Station newDownStation) {
        if (!isResistedStation(newUpStation) && !isResistedStation(newDownStation)) {
            throw new ResisterSectionException("[ERROR] 등록하려는 구간의 상행선 또는 하행선 중 한개는 노선에 존재해야합니다.");
        }

        if (isResistedStation(newUpStation) && isResistedStation(newDownStation)) {
            throw new ResisterSectionException("[ERROR] 등록하려는 구간의 상행선 또는 하행선 중 한개만 노선에 존재해야합니다.");
        }
    }

    private boolean isResistedStation(final Station station) {
        return value.stream()
                .anyMatch(section ->
                        section.getDownStation().equals(station)
                                || section.getUpStation().equals(station)
                );
    }

    private SectionsUpdateResult addDownDirectionSection(final Station newUpStation,
                                                         final Station newDownStation,
                                                         final Integer distance) {
        if (isUpStation(newUpStation)) {
            return splitInsertDownDirection(newUpStation, newDownStation, distance);
        }
        return simpleAddLast(newUpStation, newDownStation, distance);
    }

    private SectionsUpdateResult simpleAddLast(final Station newUpStation, final Station newDownStation,
                                               final Integer distance) {
        final List<Section> addedSections = new ArrayList<>();
        final Section section = Section.createWithoutId(newUpStation, newDownStation, distance);
        value.addLast(section);
        addedSections.add(section);

        return new SectionsUpdateResult(new ArrayList<>(), addedSections);
    }

    private boolean isUpStation(final Station station) {
        return value.stream()
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    private SectionsUpdateResult splitInsertDownDirection(final Station newUpStation,
                                                          final Station newDownStation,
                                                          final Integer distance) {
        final Section oldSection = findSectionThisUpStation(newUpStation);
        validateDistance(distance, oldSection);
        final int oldSectionIndex = value.indexOf(oldSection);
        final Section frontSection = Section.createWithoutId(newUpStation, newDownStation, distance);
        final Section backSection = Section.createWithoutId(
                newDownStation,
                oldSection.getDownStation(),
                oldSection.getDistance() - distance
        );
        return splitSection(oldSectionIndex, frontSection, backSection);
    }

    private SectionsUpdateResult addUpDirectionSection(final Station newUpStation,
                                                       final Station newDownStation,
                                                       final Integer distance) {
        if (isDownStation(newDownStation)) {
            return splitInsertUpDirection(newUpStation, newDownStation, distance);
        }
        return simpleAddFirst(newUpStation, newDownStation, distance);
    }

    private SectionsUpdateResult simpleAddFirst(final Station newUpStation,
                                                final Station newDownStation,
                                                final Integer distance) {
        final List<Section> addedSections = new ArrayList<>();
        final Section section = Section.createWithoutId(newUpStation, newDownStation, distance);
        value.addFirst(section);
        addedSections.add(section);
        return new SectionsUpdateResult(new ArrayList<>(), addedSections);
    }

    private boolean isDownStation(final Station station) {
        return value.stream()
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    private SectionsUpdateResult splitInsertUpDirection(final Station newUpStation,
                                                        final Station newDownStation,
                                                        final Integer distance) {
        final Section oldSection = findSectionThisDownStation(newDownStation);
        validateDistance(distance, oldSection);
        final int oldSectionIndex = value.indexOf(oldSection);
        final Section frontSection = Section.createWithoutId(
                oldSection.getUpStation(),
                newUpStation,
                oldSection.getDistance() - distance);
        final Section backSection = Section.createWithoutId(newUpStation, newDownStation, distance);
        return splitSection(oldSectionIndex, frontSection, backSection);
    }

    private void validateDistance(final Integer distance, final Section oldSection) {
        if (distance >= oldSection.getDistance()) {
            throw new ResisterSectionException("[ERROR] 역 사이에 새 역을 등록할 경우엔 길이가 원래 있던 길이보다 짧아야합니다.");
        }
    }

    private SectionsUpdateResult splitSection(final int oldSectionIndex,
                                              final Section frontSection,
                                              final Section backSection) {
        final List<Section> deletedSections = new ArrayList<>();
        final List<Section> addedSections = new ArrayList<>();
        deletedSections.add(value.remove(oldSectionIndex));
        value.add(oldSectionIndex, frontSection);
        value.add(oldSectionIndex + 1, backSection);
        addedSections.add(frontSection);
        addedSections.add(backSection);

        return new SectionsUpdateResult(deletedSections, addedSections);
    }

    public SectionsUpdateResult removeStation(final Station station) {
        validateRemoveStation(station);
        if (isBetween(station)) {
            return removeStationBetween(station);
        }
        if (isUpStation(station)) {
            return simpleRemoveFirst();
        }
        return simpleRemoveLast();
    }

    private void validateRemoveStation(final Station station) {
        if (!isUpStation(station) && !isDownStation(station)) {
            throw new NotFoundStationException("[ERROR] 해당 지하철역이 존재하지 않습니다.");
        }
        if (value.size() <= MINIMUM_SIZE) {
            throw new RemoveSectionException("[ERROR] 구간이 한개일 경우엔 삭제할 수 없습니다.");
        }
    }

    private boolean isBetween(final Station station) {
        return isUpStation(station) && isDownStation(station);
    }

    private SectionsUpdateResult removeStationBetween(final Station station) {
        final List<Section> deletedSections = new ArrayList<>();
        final Section frontSection = findSectionThisDownStation(station);
        final int sectionIndex = value.indexOf(frontSection);
        value.remove(frontSection);
        deletedSections.add(frontSection);
        final Section backSection = removeOldBackSection(station, deletedSections);

        final List<Section> addedSections = addNewMergedSection(sectionIndex, frontSection, backSection);
        return new SectionsUpdateResult(deletedSections, addedSections);
    }

    private Section removeOldBackSection(final Station station, final List<Section> deletedSections) {
        final Section backSection = findSectionThisUpStation(station);
        value.remove(backSection);
        deletedSections.add(backSection);
        return backSection;
    }

    private List<Section> addNewMergedSection(final int sectionIndex, final Section frontSection,
                                              final Section backSection) {
        final List<Section> addedSections = new ArrayList<>();
        final Section newSection = mergeSection(frontSection, backSection);
        value.add(sectionIndex, newSection);
        addedSections.add(newSection);
        return addedSections;
    }

    private Section findSectionThisUpStation(final Station station) {
        return value.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny()
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 해당 구간이 존재하지 않습니다."));
    }

    private Section findSectionThisDownStation(final Station station) {
        return value.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny()
                .orElseThrow(() -> new NotFoundStationException("[ERROR] 해당 구간이 존재하지 않습니다."));
    }

    private Section mergeSection(final Section frontSection, final Section backSection) {
        return Section.createWithoutId(
                frontSection.getUpStation(),
                backSection.getDownStation(),
                frontSection.getDistance() + backSection.getDistance()
        );
    }

    private SectionsUpdateResult simpleRemoveFirst() {
        final List<Section> deletedSections = new ArrayList<>();
        deletedSections.add(value.removeFirst());
        return new SectionsUpdateResult(deletedSections, new ArrayList<>());
    }

    private SectionsUpdateResult simpleRemoveLast() {
        final List<Section> deletedSections = new ArrayList<>();
        deletedSections.add(value.removeLast());
        return new SectionsUpdateResult(deletedSections, new ArrayList<>());
    }

    public List<Station> getStations() {
        final List<Station> stations = value.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(value.getLast().getDownStation());

        return Collections.unmodifiableList(stations);
    }
}
