package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotFoundException;

public class Sections {

    private static final String NOT_EXISTING_LINE_EXCEPTION = "존재하지 않는 노선입니다.";
    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";
    private static final String ALL_STATIONS_REGISTERED_EXCEPTION = "이미 노선에 등록된 지하철역들입니다.";
    private static final String NO_STATION_REGISTERED_EXCEPTION = "적어도 하나의 지하철역은 이미 노선에 등록되어 있어야 합니다.";

    private final List<Section> value;

    private Sections(List<Section> value) {
        validateLineExistence(value);
        this.value = value;
    }

    public static Sections of(List<Section> sections) {
        validateLineExistence(sections);
        List<Station> sortedStations = toSortedStationList(SectionStationMap.of(sections));
        return new Sections(toSortedSections(sortedStations, sections));
    }

    private static void validateLineExistence(List<Section> value) {
        if (value.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_LINE_EXCEPTION);
        }
    }

    private static List<Station> toSortedStationList(SectionStationMap sectionMap) {
        LinkedList<Station> list = new LinkedList<>();
        Station upperEndStation = sectionMap.findUpperEndStation();
        list.add(upperEndStation);

        Long current = upperEndStation.getId();
        while (sectionMap.hasDownStation(current)) {
            Station nextStation = sectionMap.getDownStationIdOf(current);
            list.add(nextStation);
            current = nextStation.getId();
        }
        return list;
    }

    private static List<Section> toSortedSections(List<Station> sortedStations,
                                                  List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            sortedSections.add(findSectionByUpStation(sortedStations.get(i), sections));
        }
        return sortedSections;
    }

    private static Section findSectionByUpStation(Station upStation,
                                                  List<Section> sections) {
        return sections.stream()
                .filter(section -> section.hasUpStationOf(upStation))
                .findFirst()
                .get();
    }

    public Sections save(Section newSection) {
        validateSingleRegisteredStation(newSection);
        List<Section> sections = new ArrayList<>(value);
        if (!isEndSection(newSection)) {
            updateOriginalSection(newSection, sections);
        }
        sections.add(newSection);
        return new Sections(sections);
    }

    private void updateOriginalSection(Section newSection, List<Section> sections) {
        boolean isRegisteredUpStation = isRegistered(newSection.getUpStation());
        if (isRegisteredUpStation) {
            Section oldSection = getLowerSection(newSection.getUpStation());
            sections.remove(oldSection);
            Section updatedSection = new Section(newSection.getDownStation(),
                    oldSection.getDownStation(),
                    oldSection.toRemainderDistance(newSection));
            sections.add(updatedSection);
            return;
        }
        Section oldSection = getUpperSection(newSection.getDownStation());
        sections.remove(oldSection);
        Section updatedSection = new Section(oldSection.getUpStation(),
                newSection.getUpStation(),
                oldSection.toRemainderDistance(newSection));
        sections.add(updatedSection);
    }

    private void validateSingleRegisteredStation(Section section) {
        boolean isRegisteredUpStation = isRegistered(section.getUpStation());
        boolean isRegisteredDownStation = isRegistered(section.getDownStation());
        if (isRegisteredUpStation && isRegisteredDownStation) {
            throw new IllegalArgumentException(ALL_STATIONS_REGISTERED_EXCEPTION);
        }
        if (!isRegisteredUpStation && !isRegisteredDownStation) {
            throw new IllegalArgumentException(NO_STATION_REGISTERED_EXCEPTION);
        }
    }

    private boolean isEndSection(Section section) {
        boolean isNewUpperEndSection = value.get(0)
                .hasUpStationOf(section.getDownStation());
        boolean isNewLowerEndSection = value.get(value.size() - 1)
                .hasDownStationOf(section.getUpStation());

        return isNewUpperEndSection || isNewLowerEndSection;
    }

    public Sections delete(Station station) {
        validateRegisteredStation(station);
        validateNotLastSection();
        List<Section> sections = new ArrayList<>(value);
        if (isMiddleStation(station)) {
            return removeMiddleStation(station, sections);
        }
        sections.removeIf(section -> section.hasStationOf(station));
        return new Sections(sections);
    }

    private void validateRegisteredStation(Station station) {
        if (!isRegistered(station)) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
    }

    private boolean isRegistered(Station station) {
        return value.stream()
                .anyMatch(section -> section.hasStationOf(station));
    }

    private void validateNotLastSection() {
        if (value.size() == 1) {
            throw new IllegalArgumentException(LAST_SECTION_EXCEPTION);
        }
    }

    private boolean isMiddleStation(Station station) {
        return value.stream()
                .filter(section -> section.hasStationOf(station))
                .count() == 2;
    }

    private Sections removeMiddleStation(Station station, List<Section> sections) {
        Section upperSection = getUpperSection(station);
        Section lowerSection = getLowerSection(station);
        Section connectedSection = new Section(upperSection.getUpStation(),
                lowerSection.getDownStation(),
                upperSection.toConnectedDistance(lowerSection));

        sections.removeAll(List.of(upperSection, lowerSection));
        sections.add(connectedSection);
        return new Sections(sections);
    }

    private Section getUpperSection(Station station) {
        return value.stream()
                .filter(section -> section.hasDownStationOf(station))
                .findFirst()
                .get();
    }

    private Section getLowerSection(Station station) {
        return value.stream()
                .filter(section -> section.hasUpStationOf(station))
                .findFirst()
                .get();
    }

    public List<Section> extractNewSections(Sections previousSections) {
        List<Section> previous = new ArrayList<>(previousSections.value);
        List<Section> current = new ArrayList<>(value);

        current.removeAll(previous);
        return current;
    }

    public List<Section> extractDeletedSections(Sections previousSections) {
        List<Section> previous = new ArrayList<>(previousSections.value);
        List<Section> current = new ArrayList<>(value);

        previous.removeAll(current);
        return previous;
    }
}
