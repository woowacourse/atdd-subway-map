package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.InvalidSectionException;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int FIRST_ELEMENT = 0;
    private final List<Section> sections;

    public static Sections from(Section... sections) {
        return from(new ArrayList<>(Arrays.asList(sections)));
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> asStations() {
        LinkedList<Station> sortedStation = new LinkedList<>();
        final Section pivotSection = sections.get(0);
        sortPreviousSections(sortedStation, pivotSection);
        sortFollowingSections(sortedStation, pivotSection);
        return sortedStation;
    }

    private void sortPreviousSections(LinkedList<Station> sortedSections, Section section) {
        final Station upStation = section.getUpStation();
        sortedSections.addFirst(upStation);
        findSectionByDownStation(upStation)
                .ifPresent(sec -> sortPreviousSections(sortedSections, sec));
    }

    private Optional<Section> findSectionByDownStation(Station targetStation) {
        return sections.stream()
                .filter(section -> section.isDownStation(targetStation))
                .findAny();
    }

    private void sortFollowingSections(LinkedList<Station> sortedSections, Section section) {
        final Station downStation = section.getDownStation();
        sortedSections.addLast(downStation);
        findSectionByUpStation(downStation)
                .ifPresent(sec -> sortFollowingSections(sortedSections, sec));
    }

    private Optional<Section> findSectionByUpStation(Station targetStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(targetStation))
                .findAny();
    }

    public Optional<Section> affectedSection(Section newSection) {
        List<Section> collect = sections.stream()
                .filter(originalSection -> isAdjacentSection(newSection, originalSection))
                .collect(Collectors.toList());

        if (collect.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation())) &&
                collect.stream().anyMatch(section -> section.isDownStation(newSection.getDownStation()))) {
            throw new InvalidSectionException();
        }
        if (collect.size() == 2) {
            Station upStation = newSection.getUpStation();
            if (collect.stream()
                    .anyMatch(section -> section.isUpStation(upStation))) {
                return updateSection(collect.get(1), newSection);
            }
        }
        final Section originalSection = collect.get(FIRST_ELEMENT);
        return updateSection(originalSection, newSection);
    }

    private boolean isAdjacentSection(Section newSection, Section originalSection) {
        if (originalSection.isSameSection(newSection)) {
            throw new DuplicatedSectionException();
        }

        return originalSection.isUpStation(newSection.getUpStation()) ||
                originalSection.isDownStation(newSection.getDownStation()) ||
                originalSection.isUpStation(newSection.getDownStation()) ||
                originalSection.isDownStation(newSection.getUpStation());
    }

    private Optional<Section> updateSection(Section originalSection, Section newSection) {

        final Station upStation = newSection.getUpStation();
        final Station downStation = newSection.getDownStation();

        if (originalSection.isUpStation(upStation)) {
            originalSection.updateUpStation(newSection);
            return Optional.of(originalSection);
        }

        if (originalSection.isDownStation(downStation)) {
            originalSection.updateDownStation(newSection);
            return Optional.of(originalSection);
        }

        return Optional.empty();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }

    public Optional<Section> transformSection(Long stationId) {
        if (sections.size() == 1) {
            return Optional.empty();
        }
        Station downStation = null;
        Station upStation = null;
        int distance = 0;
        for (Section section : sections) {
            if (section.isUpStationId(stationId)) {
                downStation = section.getDownStation();
            }
            if (section.isDownStationId(stationId)) {
                upStation = section.getUpStation();
            }
            distance += section.getDistance();
        }

        return Optional.of(Section.create(upStation, downStation, distance));
    }

    public boolean hasSize(int size) {
        return sections.size() == size;
    }
}
