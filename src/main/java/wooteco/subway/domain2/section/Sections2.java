package wooteco.subway.domain2.section;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import wooteco.subway.domain2.station.Station;
import wooteco.subway.exception.NotFoundException;

public class Sections2 {

    private static final String NOT_EXISTING_LINE_EXCEPTION = "존재하지 않는 노선입니다.";
    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String LAST_SECTION_EXCEPTION = "노선의 마지막 구간은 제거할 수 없습니다.";

    private final List<Section> value;

    private Sections2(List<Section> value) {
        validateLineExistence(value);
        this.value = value;
    }

    public static Sections2 of(List<Section> sections) {
        validateLineExistence(sections);
        List<Station> sortedStations = toSortedStationList(SectionStationMap2.of(sections));
        return new Sections2(toSortedSections(sortedStations, sections));
    }

    private static void validateLineExistence(List<Section> value) {
        if (value.isEmpty()) {
            throw new NotFoundException(NOT_EXISTING_LINE_EXCEPTION);
        }
    }

    private static List<Station> toSortedStationList(SectionStationMap2 sectionMap) {
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

    public Sections2 delete(Station station) {
        validateRegisteredStation(station);
        validateNotLastSection();
        List<Section> sections = new ArrayList<>(value);
        if (isMiddleStation(station)) {
            return removeMiddleStation(station, sections);
        }
        sections.removeIf(section -> section.hasStationOf(station));
        return new Sections2(sections);
    }

    private void validateRegisteredStation(Station station) {
        boolean isRegistered = value.stream()
                .anyMatch(section -> section.hasStationOf(station));
        if (!isRegistered) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
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

    private Sections2 removeMiddleStation(Station station, List<Section> sections) {
        Section upperSection = getUpperSection(station);
        Section lowerSection = getLowerSection(station);
        Section connectedSection = new Section(lowerSection.getUpStation(),
                upperSection.getDownStation(),
                upperSection.toConnectedDistance(lowerSection));

        sections.removeAll(List.of(upperSection, lowerSection));
        sections.add(connectedSection);
        return new Sections2(sections);
    }

    private Section getLowerSection(Station station) {
        return value.stream()
                .filter(section -> section.hasDownStationOf(station))
                .findFirst()
                .get();
    }

    private Section getUpperSection(Station station) {
        return value.stream()
                .filter(section -> section.hasUpStationOf(station))
                .findFirst()
                .get();
    }

    public List<Section> extractNewSections(Sections2 previousSections) {
        List<Section> previous = new ArrayList<>(previousSections.value);
        List<Section> current = new ArrayList<>(value);

        current.removeAll(previous);
        return current;
    }

    public List<Section> extractDeletedSections(Sections2 previousSections) {
        List<Section> previous = new ArrayList<>(previousSections.value);
        List<Section> current = new ArrayList<>(value);

        previous.removeAll(current);
        return previous;
    }
}
