package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import wooteco.subway.exception.badRequest.InvalidSectionException;
import wooteco.subway.exception.notFound.StationNotFoundException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    private final List<Section> sections;

    public static Sections create(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections create() {
        return new Sections(new ArrayList<>());
    }


    public void addSection(Section section) {
        sections.add(section);
    }

    public Section firstSection() {
        return sections.get(FIRST_INDEX);
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

    public Optional<Section> affectedSectionWhenInserting(Section newSection) {
        boolean existsUpStation = sections.stream()
            .anyMatch(section -> section.containsStation(newSection.getUpStation()));

        boolean existsDownStation = sections.stream()
            .anyMatch(section -> section.containsStation(newSection.getDownStation()));

        if (isNotInsertable(existsUpStation, existsDownStation)) {
            throw new InvalidSectionException();
        }

        return updateSection(newSection, existsUpStation, existsDownStation);
    }

    private Optional<Section> updateSection(Section newSection, boolean existsUpStation,
        boolean existsDownStation) {
        Optional<Section> foundSection = Optional.empty();
        if (existsUpStation) {
            foundSection = sections.stream()
                .filter(section -> section.isUpStation(newSection.getUpStation())).findAny();
            foundSection.ifPresent(section -> section.updateUpStation(newSection));
        }
        if (existsDownStation) {
            foundSection = sections.stream()
                .filter(section -> section.isDownStation(newSection.getDownStation())).findAny();
            foundSection.ifPresent(section -> section.updateDownStation(newSection));
        }
        return foundSection;
    }

    private boolean isNotInsertable(boolean existsUpStation, boolean existsDownStation) {
        return (existsUpStation && existsDownStation) ||
            (!existsUpStation && !existsDownStation);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public Optional<Section> affectedSectionWhenRemoving(Long stationId) {
        final List<Section> sections = this.sections.stream()
            .filter(section -> section.containsStation(stationId))
            .collect(Collectors.toList());

        if(stationNotExist(sections)) {
            throw new StationNotFoundException();
        }

        if (isEndStation(sections)) {
            return Optional.empty();
        }

        Section firstSection = sections.get(FIRST_INDEX);
        Section secondSection = sections.get(SECOND_INDEX);
        firstSection.combineSection(secondSection);
        return Optional.of(firstSection);
    }

    private boolean stationNotExist(List<Section> sections) {
        return sections.size() == 0;
    }

    private boolean isEndStation(List<Section> sections) {
        return sections.size() == 1;
    }
}
