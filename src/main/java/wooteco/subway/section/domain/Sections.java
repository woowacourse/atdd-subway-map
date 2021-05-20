package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.exception.DeleteSectionException;
import wooteco.subway.exception.DuplicatedSectionException;
import wooteco.subway.exception.NotAddSectionException;
import wooteco.subway.exception.NotContainStationsException;
import wooteco.subway.exception.NotFoundTerminalStationException;
import wooteco.subway.exception.NotRemoveSectionException;
import wooteco.subway.station.domain.Station;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.sections = sortedSections(sections);
    }

    private List<Section> sortedSections(List<Section> sections) {
        List<Section> sortedSection = new ArrayList<>();
        HashMap<Long, Section> sectionByUpStationId = sectionByUpStationId(sections);
        Station currentStation = upTerminalStation(sections);
        for (int i = 0; i < sections.size(); i++) {
            Section nextSection = sectionByUpStationId.getOrDefault(currentStation.getId(), null);
            if (nextSection != null) {
                sortedSection.add(nextSection);
                currentStation = nextSection.getDownStation();
            }
        }
        return sortedSection;
    }

    private HashMap<Long, Section> sectionByUpStationId(List<Section> sections) {
        return new HashMap<>(
            sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section))
        );
    }

    private Station upTerminalStation(List<Section> sections) {
        List<Long> upStationIds = upStationIds(sections);
        List<Long> downStationIds = downStationIds(sections);

        upStationIds.removeAll(downStationIds);
        Long upTerminalStationId = upStationIds.get(0);
        return sections.stream()
            .filter(section -> section.getUpStationId().equals(upTerminalStationId))
            .map(Section::getUpStation)
            .findFirst()
            .orElseThrow(NotFoundTerminalStationException::new);
    }

    private List<Long> upStationIds(List<Section> sections) {
        return sections.stream()
            .map(Section::getUpStationId)
            .collect(Collectors.toList());
    }

    private List<Long> downStationIds(List<Section> sections) {
        return sections.stream()
            .map(Section::getDownStationId)
            .collect(Collectors.toList());
    }

    public boolean isTerminalSection(Section section) {
        return isEqualsTerminalUpSection(section)
            || isEqualsTerminalDownSection(section);
    }

    private boolean isEqualsTerminalUpSection(Section section) {
        return sections.get(0).isPreviousSection(section);
    }

    private boolean isEqualsTerminalDownSection(Section section) {
        return sections.get(sections.size() - 1).isNextSection(section);
    }

    public boolean isTerminalStation(Station station) {
        return sections.get(0).isEqualsUpStation(station)
            || sections.get(sections.size() - 1).isEqualsDownStation(station);
    }

    public void validateAddable(Section section) {
        validateDuplicatedSection(section);
        validateExistStation(section);
    }

    public void validateDuplicatedSection(Section section) {
        if (sections.stream()
            .anyMatch((it -> it.isContainEqualsStations(section)))) {
            throw new DuplicatedSectionException();
        }
    }

    public void validateExistStation(Section section) {
        List<Station> stations = sortedStations();

        if (!stations.contains(section.getUpStation())
            && !stations.contains(section.getDownStation())) {
            throw new NotContainStationsException();
        }
    }

    public void validateRemovable(Station station) {
        validateSectionsSize();
        validateExistStation(station);
    }

    public void validateSectionsSize() {
        if (sections.size() == 1) {
            throw new DeleteSectionException();
        }
    }

    public void validateExistStation(Station station) {
        List<Station> stations = sortedStations();
        if (!stations.contains(station)) {
            throw new NotContainStationsException();
        }
    }

    public Section createdSectionByAddInternalSection(Section section) {
        for (Section currentSection : sections) {
            if (currentSection.isEqualsUpStation(section)) {
                return currentSection.createdDownSection(section);
            }
            if (currentSection.isEqualsDownStation(section)) {
                return currentSection.createdUpSection(section);
            }
        }
        throw new NotAddSectionException();
    }

    public Section removedSectionByAddInternalSection(Section section) {
        return sections.stream()
            .filter((it) -> it.isContainEqualsStation(section))
            .findAny()
            .orElseThrow(NotAddSectionException::new);
    }

    public Section createdSectionByRemoveInternalStation(Long lineId, Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (currentSection.isEqualsUpStation(station)) {
                Section prevSection = sections.get(i - 1);
                int newDistance = prevSection.getDistance() + currentSection.getDistance();
                return new Section(lineId, prevSection.getUpStation(),
                    currentSection.getDownStation(), newDistance);
            }
        }
        throw new NotRemoveSectionException();
    }

    public List<Section> removedSectionsByRemoveInternalStation(Station station) {
        List<Section> removedSections = new ArrayList<>();
        for (Section section : sections) {
            if (section.isEqualsUpStation(station) || section.isEqualsDownStation(station)) {
                removedSections.add(section);
            }
        }
        return removedSections;
    }

    public Section removedSectionByRemoveTerminalStation(Station station) {
        Section upTerminalSection = sections.get(0);
        if (upTerminalSection.isEqualsUpStation(station)) {
            return upTerminalSection;
        }

        Section downTerminalSection = sections.get(sections.size() - 1);
        if (downTerminalSection.isEqualsDownStation(station)) {
            return downTerminalSection;
        }
        throw new NotRemoveSectionException();
    }

    public List<Station> sortedStations() {
        List<Station> stations = new ArrayList<>();
        for (Section each : sections) {
            stations.add(each.getUpStation());
        }
        stations.add(sections.get(stations.size() - 1).getDownStation());
        return stations;
    }

}