package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values;

    public Sections(Section section) {
        values = new LinkedList<>();
        values.add(section);
    }

    public Sections(List<Section> sections) {
        values = new LinkedList<>(sortedSections(sections));
    }

    private List<Section> sortedSections(List<Section> sections) {
        if (sections.size() <= 1) {
            return sections;
        }
        Long upDestinationId = findUpDestinationId(sections);
        Map<Long, Section> sectionsByIds = sections.stream()
            .collect(Collectors.toMap(Section::getUpStationId, section -> section, (a, b) -> b));

        return getSortedValuesBy(upDestinationId, (key) -> sectionsByIds.get(key).getDownStationId(), sectionsByIds);
    }

    private Long findUpDestinationId(List<Section> sections) {
        List<Long> upStations = collectValuesBy(Section::getUpStationId, sections);
        List<Long> downStations = collectValuesBy(Section::getDownStationId, sections);

        upStations.removeAll(downStations);
        if (upStations.size() != 1) {
            throw new IllegalStateException("저장된 구간들 값이 올바르지 않습니다.");
        }
        return upStations.get(0);
    }

    private <T, E> List<T> collectValuesBy(Function<E, T> mapper, List<E> collection) {
        return collection.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }

    private <T> List<T> getSortedValuesBy(Long firstKey, Function<Long, Long> newKeyMapper, Map<Long, T> values) {
        List<T> result = new ArrayList<>();
        Long key = firstKey;

        while (values.containsKey(key)) {
            result.add(values.get(key));
            key = newKeyMapper.apply(key);
        }

        return result;
    }

    public Section add(Section newSection) {
        validateAddable(newSection);

        if (getUpDestination().equals(newSection.getDownStation())) {
            values.add(0, newSection);
            return newSection;
        }

        if (getDownDestination().equals(newSection.getUpStation())) {
            values.add(newSection);
            return newSection;
        }

        for (Section section : values) {
            if (section.hasSameUpStation(newSection)) {
                return insertAfterUpStation(newSection, section);
            }

            if (section.hasSameDownStation(newSection)) {
                return insertBeforeDownStation(newSection, section);
            }
        }
        throw new IllegalStateException("구간을 추가할 수 있는 상태가 아닙니다.");
    }

    private Section insertAfterUpStation(Section newSection, Section section) {
        int index = values.indexOf(section);
        Section sectionToModify = new Section(section.getId(), newSection.getDownStation(), section.getDownStation(),
            getNewDistance(section, newSection));
        values.set(index, newSection);
        values.add(index + 1, sectionToModify);
        return sectionToModify;
    }

    private int getNewDistance(Section section, Section newSection) {
        int newDistance = section.getDistance() - newSection.getDistance();
        if (newDistance <= 0) {
            throw new IllegalArgumentException("추가하려는 구간의 거리가 기존 구간의 거리보다 작아야합니다.");
        }
        return newDistance;
    }

    private Section insertBeforeDownStation(Section newSection, Section section) {
        int index = values.indexOf(section);
        Section sectionToModify = new Section(section.getId(), section.getUpStation(), newSection.getUpStation(),
            getNewDistance(section, newSection));
        values.set(index, sectionToModify);
        values.add(index + 1, newSection);
        return sectionToModify;
    }

    private void validateAddable(Section section) {
        List<Section> foundSections = findSectionsOverlapped(section);

        if (isInvalid(section, foundSections)) {
            throw new IllegalArgumentException("이미 포함된 두 역을 가진 Section 을 추가할 수 없습니다.");
        }
    }

    private List<Section> findSectionsOverlapped(Section newSection) {
        return values.stream()
            .filter(section ->
                section.contains(newSection.getDownStation()) || section.contains(newSection.getUpStation()))
            .collect(Collectors.toList());
    }

    private boolean isInvalid(Section section, List<Section> foundSections) {
        return hasSameStations(section) ||
            isInvalidCountInMiddle(foundSections) || isSameWithDestinations(foundSections);
    }

    private boolean hasSameStations(Section newSection) {
        return values.stream().anyMatch(section -> section.hasSameStations(newSection));
    }

    private boolean isInvalidCountInMiddle(List<Section> foundSections) {
        int count = foundSections.size();
        return count < 1 || count > 2;
    }

    private boolean isSameWithDestinations(List<Section> foundSections) {
        return values.size() > 2 && foundSections.size() == 2 &&
            foundSections.containsAll(List.of(values.get(0), values.get(values.size() - 1)));
    }

    public Station getUpDestination() {
        return values.get(0).getUpStation();
    }

    public Station getDownDestination() {
        return values.get(values.size() - 1).getDownStation();
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }

    public Section delete(Station station) {
        validateDeletable();

        if (station.equals(getUpDestination())) {
            return values.remove(0);
        }

        if (station.equals(getDownDestination())) {
            return values.remove(values.size() - 1);
        }

        return mergeTwoSections(findSectionToDelete(station));
    }

    private void validateDeletable() {
        if (values.size() <= 1) {
            throw new IllegalArgumentException("구간이 하나인 경우 삭제할 수 없습니다.");
        }
    }

    private Section findSectionToDelete(Station station) {
        return values.stream()
            .filter(it -> it.contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("삭제할 역이 존재하지 않습니다."));
    }

    private Section mergeTwoSections(Section section) {
        int index = values.indexOf(section);
        Section nextSection = findNextSection(section);
        Section modifiedSection = values.set(index,
            new Section(nextSection.getId(), section.getUpStation(), nextSection.getDownStation(),
                section.getDistance() + nextSection.getDistance()));
        values.remove(index + 1);
        return modifiedSection;
    }

    private Section findNextSection(Section section) {
        return values.get(values.indexOf(section) + 1);
    }

    public List<Section> getDifference(List<Section> sections) {
        List<Section> result = new ArrayList<>(values);
        result.removeAll(sections);
        return result;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(getUpDestination());
        for (Section section : values) {
            stations.add(section.getDownStation());
        }
        return stations;
    }
}
