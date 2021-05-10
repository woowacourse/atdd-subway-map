package wooteco.subway.section.domain;

import wooteco.subway.section.exception.SectionsIllegalArgumentException;
import wooteco.subway.section.exception.SectionsSizeTooSmallException;
import wooteco.subway.station.domain.Station;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

public class Sections {
    private static final int SECTIONS_MINIMUM_SIZE = 1;
    private static final int END_TERMINAL_SIZE = 1;
    private final LinkedList<Section> sections;

    public Sections(List<Section> sections) {
        this(new LinkedList<>(sections));
    }

    public Sections(LinkedList<Section> sections) {
        checkMinimumSize(sections);
        sort(sections);
        this.sections = sections;
    }

    private void checkMinimumSize(List<Section> sections) {
        if (sections.size() <= SECTIONS_MINIMUM_SIZE) {
            throw new SectionsSizeTooSmallException(String.format("최소 %d 이상이어야 합니다. 현재 사이즈 : %d, ", SECTIONS_MINIMUM_SIZE, sections.size()));
        }
    }

    private void sort(List<Section> sections) {
        LinkedList<Section> sorted = new LinkedList<>();
        findFirstSection(sections);
    }

    private Section findFirstSection(List<Section> sections) {
        Set<Station> upStations = toStationSet(sections, Section::getUpStation);
        Set<Station> downStations = toStationSet(sections, Section::getDownStation);

        removeDuplicateStation(upStations, downStations);

        checkSizeOfEndStation(upStations, downStations);
        return findSectionByStation(sections, upStations.iterator().next());
    }

    private void removeDuplicateStation(Set<Station> upStations, Set<Station> downStations) {
        Set<Station> upStationsCopy = new HashSet<>(upStations);
        upStations.removeAll(downStations);
        downStations.removeAll(upStationsCopy);
    }

    private Section findSectionByStation(List<Section> sections, Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny()
                .orElseThrow(() -> new SectionsIllegalArgumentException(
                        String.format("노선 목록에 해당 역을 가진 노선이 없습니다. 역 : %s", station)));
    }

    private void checkSizeOfEndStation(Set<Station> upStations, Set<Station> downStations) {
        if (upStations.size() != END_TERMINAL_SIZE || downStations.size() != END_TERMINAL_SIZE) {
            throw new SectionsIllegalArgumentException(
                    String.format("종점의 숫자가 %d개가 아닙니다. 상행 종점 갯수 : %d, 하행 종점 갯수 : %d",
                            END_TERMINAL_SIZE, upStations.size(), downStations.size()));
        }
    }

    private Set<Station> toStationSet(List<Section> sections, Function<Section, Station> toStationFunction) {
        return sections.stream()
                .map(toStationFunction)
                .collect(toSet());
    }


}
