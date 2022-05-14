package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.exception.CannotConnectSectionException;
import wooteco.subway.exception.SectionDuplicateException;
import wooteco.subway.exception.StationNotFoundException;

public class Sections {

    private static final int MIN_REMOVE_SIZE = 2;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
        validateConnectSection(section);
        if (findNearbySection(section).isPresent()) {
            updateSection(findNearbySection(section).get(), section);
        }
        sections.add(section);
    }

    private void validateDuplicateSection(final Section checkSection) {
        boolean isDuplicate = sections.stream()
                .anyMatch(section -> section.isDuplicateSection(checkSection));
        if (isDuplicate) {
            throw new SectionDuplicateException();
        }
    }

    private void validateConnectSection(final Section checkSection) {
        sections.stream()
                .filter(section -> section.hasSectionToConnect(checkSection))
                .findFirst()
                .orElseThrow(CannotConnectSectionException::new);
    }

    private Optional<Section> findNearbySection(final Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().isSameStation(newSection.getUpStation())
                        || section.getDownStation().isSameStation(newSection.getDownStation()))
                .findFirst();
    }

    private void updateSection(final Section foundSection, final Section newSection) {
        final Station upStation = foundSection.getUpStation();
        final Station downStation = foundSection.getDownStation();

        if (upStation.isSameStation(newSection.getUpStation())) {
            foundSection.updateSection(newSection.getDownStation(), downStation, newSection.getDistance());
        }
        if (downStation.isSameStation(newSection.getDownStation())) {
            foundSection.updateSection(upStation, newSection.getUpStation(), newSection.getDistance());
        }
    }

    public List<Section> delete(final Station station) {
        if (sections.size() < MIN_REMOVE_SIZE) {
            throw new IllegalStateException("역을 삭제할 수 없습니다.");
        }
        final List<Section> deleteSections = new ArrayList<>();
        sections.forEach(section -> {
            final boolean isSameUpStation = section.getUpStation().isSameStation(station);
            final boolean isSameDownStation = section.getDownStation().isSameStation(station);
            if (isSameUpStation || isSameDownStation) {
                deleteSections.add(section);
            }
        });
        deleteSections.forEach(sections::remove);
        return deleteSections;
    }

    public List<Station> sortByStation() {
        final List<Station> sortedStations = new ArrayList<>();
        Station currentStation = findFirstStation();
        sortedStations.add(currentStation);
        while (hasNextStation(currentStation)) {
            final Station next = nextStation(currentStation);
            sortedStations.add(next);
            currentStation = next;
        }
        return sortedStations;
    }

    private boolean hasNextStation(final Station currentStation) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().isSameStation(currentStation));
    }

    private Station findFirstStation() {
        final List<Station> upStations = findAllUpStation();
        final List<Station> downStations = findAllDownStation();
        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private Station findLastStation() {
        final List<Station> upStations = findAllUpStation();
        final List<Station> downStations = findAllDownStation();
        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private Station nextStation(final Station currentStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().isSameStation(currentStation))
                .findFirst()
                .map(Section::getDownStation)
                .orElseThrow(StationNotFoundException::new);
    }

    private List<Station> findAllUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> findAllDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
