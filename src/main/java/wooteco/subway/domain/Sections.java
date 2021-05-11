package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.InvalidSectionException;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int SECTION_LIMIT = 1;
    private static final int AFFECTED_SECTION_LIMIT = 2;
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
        if (sections.size() < SECTION_LIMIT) {
            return sortedStation;
        }
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

    public Optional<Section> changeSection(Section newSection) {
        List<Section> collect = sections.stream()
                .filter(originalSection -> isAdjacentSection(newSection, originalSection))
                .collect(Collectors.toList());
        return changedSection(newSection, collect);
    }

    private Optional<Section> changedSection(Section newSection, List<Section> sections) {
        if (sections.size() < SECTION_LIMIT || sections.size() > AFFECTED_SECTION_LIMIT) {
            throw new InvalidSectionException();
        }

        validateContainsBothStation(newSection, sections);

        if (sections.size() == SECTION_LIMIT) {
            final Section originalSection = sections.get(FIRST_ELEMENT);
            return updateSection(originalSection, newSection);
        }

        return affectedSection(newSection);
    }

    private void validateContainsBothStation(Section newSection, List<Section> sections) {
        Optional<Section> upStation = sections.stream()
                .filter(section -> section.hasStation(newSection.getUpStation().getId()))
                .findAny();
        Optional<Section> downStation = sections.stream()
                .filter(section -> section.hasStation(newSection.getDownStation().getId()))
                .findAny();

        if (upStation.isPresent() && downStation.isPresent()) {
            throw new InvalidSectionException();
        }
    }

    private Optional<Section> affectedSection(Section newSection) {
        Optional<Section> sectionByUpStation = sections.stream()
                .filter(section -> section.isUpStation(newSection.getUpStation()))
                .findAny();
        if (sectionByUpStation.isPresent()) {
            Section section = sectionByUpStation.get();
            section.updateId(section);
            section.updateUpStation(newSection);
            return Optional.of(section);
        }
        Optional<Section> sectionByDownStation = sections.stream()
                .filter(section -> section.isDownStation(newSection.getDownStation()))
                .findAny();
        if (sectionByDownStation.isPresent()) {
            Section section = sectionByDownStation.get();
            section.updateId(section);
            section.updateDownStation(newSection);
            return Optional.of(section);
        }
        return Optional.empty();
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
        if (sections.size() == SECTION_LIMIT) {
            return Optional.empty();
        }

        Station downStation = null;
        Station upStation = null;
        int distance = 0;
        for (Section section : sections) {
            if (section.isUpStationId(stationId)) {
                downStation = section.getDownStation();
            } else {
                upStation = section.getUpStation();
            }
            distance += section.getDistance();

        }

        return Optional.of(Section.of(upStation, downStation, distance));
    }

    public boolean hasSize(int size) {
        return sections.size() == size;
    }

    public List<Section> containsStationByStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.hasStation(stationId))
                .collect(Collectors.toList());
    }
}
