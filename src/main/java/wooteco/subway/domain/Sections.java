package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> values = new LinkedList<>();

    public Sections(Section section) {
        values.add(section);
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
                values.add(index + 1, new Section(newSection.getDownStation(), section.getDownStation(),
                    section.getDistance() - newSection.getDistance()));
                return;
            }

            if (section.getDownStation().equals(newSection.getDownStation())) {
                int index = values.indexOf(section);
                values.set(index, new Section(section.getUpStation(), newSection.getUpStation(),
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
        return foundSections.size() == 2 &&
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
}
