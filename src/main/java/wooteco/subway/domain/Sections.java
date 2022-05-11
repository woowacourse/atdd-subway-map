package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values = new LinkedList<>();

    public Sections(Section section) {
        values.add(section);
    }

    public Sections(List<Section> sections) {
        values.addAll(sections);
    }

    public void add(Section newSection) {
        validateAddable(newSection);

        if (getUpDestination().equals(newSection.getDownStation())) {
            values.add(0, newSection);
            return;
        }

        if (getDownDestination().equals(newSection.getUpStation())) {
            values.add(newSection);
            return;
        }

        for (Section section : values) {
            if (section.getUpStation().equals(newSection.getUpStation())) {
                int index = values.indexOf(section);
                values.set(index, newSection);
                values.add(index + 1,
                    new Section(section.getId(), newSection.getDownStation(), section.getDownStation(),
                        section.getDistance() - newSection.getDistance()));
                return;
            }

            if (section.getDownStation().equals(newSection.getDownStation())) {
                int index = values.indexOf(section);
                values.set(index, new Section(section.getId(), section.getUpStation(), newSection.getUpStation(),
                    section.getDistance() - newSection.getDistance()));
                values.add(index + 1, newSection);
                return;
            }
        }
    }

    private void validateAddable(Section section) {
        List<Section> foundSections = findSectionsOverlapped(section);

        if (isInvalid(section, foundSections)) {
            throw new IllegalArgumentException("이미 포함된 두 역을 가진 Section 을 추가할 수 없습니다.");
        }
    }

    private boolean isInvalid(Section section, List<Section> foundSections) {
        return hasSameStations(section) ||
            isContainStationsInMiddle(foundSections) || isSameWithDestinations(foundSections);
    }

    private boolean hasSameStations(Section newSection) {
        return values.stream().anyMatch(section -> section.hasSameStations(newSection));
    }

    private List<Section> findSectionsOverlapped(Section newSection) {
        return values.stream()
            .filter(section ->
                section.contains(newSection.getDownStation()) || section.contains(newSection.getUpStation()))
            .collect(Collectors.toList());
    }

    private boolean isContainStationsInMiddle(List<Section> foundSections) {
        return foundSections.size() > 2;
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

    public void delete(Station station) {
        validateDeletable();

        if (station.equals(getUpDestination())) {
            values.remove(0);
            return;
        }

        if (station.equals(getDownDestination())) {
            values.remove(values.size() - 1);
            return;
        }

        Section section = values.stream()
            .filter(it -> it.contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("삭제할 역이 존재하지 않습니다."));

        mergeTwoSections(section);
    }

    private void validateDeletable() {
        if (values.size() <= 1) {
            throw new IllegalArgumentException("구간이 하나인 경우 삭제할 수 없습니다.");
        }
    }

    private void mergeTwoSections(Section section) {
        int index = values.indexOf(section);
        Section nextSection = findNextSection(section);
        values.set(index, new Section(nextSection.getId(), section.getUpStation(), nextSection.getDownStation(),
            section.getDistance() + nextSection.getDistance()));
        values.remove(index + 1);
    }

    private Section findNextSection(Section section) {
        return values.get(values.indexOf(section) + 1);
    }
}
