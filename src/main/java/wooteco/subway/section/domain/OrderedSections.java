package wooteco.subway.section.domain;

import wooteco.subway.section.exception.SectionNotSequentialException;
import wooteco.subway.section.exception.SectionsHasDuplicateException;
import wooteco.subway.section.exception.SectionsHasNotSectionException;
import wooteco.subway.section.exception.SectionsIllegalArgumentException;
import wooteco.subway.section.exception.SectionsSizeTooSmallException;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class OrderedSections {
    private static final int SECTIONS_MINIMUM_SIZE = 1;
    private static final int END_TERMINAL_SIZE = 1;
    private static final int CAN_DUPLICATE_MINIMUM_SIZE = 0;
    private static final int BETWEEN_SECTION = 2;
    private static final int FIRST_ADJACENT_SECTION_INDEX = 0;
    private static final int SECOND_ADJACENT_SECTION_INDEX = 1;

    private final List<Section> sections;

    public OrderedSections(Section... sections) {
        this(Arrays.stream(sections).collect(toList()));
    }

    public OrderedSections(List<Section> sections) {
        checkMinimumSize(sections);
        checkDuplicateSection(sections);
        this.sections = validateAndSort(sections);
    }

    private void checkMinimumSize(List<Section> sections) {
        if (sections.size() < SECTIONS_MINIMUM_SIZE) {
            throw new SectionsSizeTooSmallException(String.format("최소 %d 이상이어야 합니다. 현재 사이즈 : %d, ", SECTIONS_MINIMUM_SIZE, sections.size()));
        }
    }

    private void checkDuplicateSection(List<Section> sections) {
        Map<Section, Long> duplicateChecker = sections.stream()
                .collect(groupingBy(identity(), counting()));

        List<String> duplicateSectionNames = duplicateChecker.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .map(Object::toString)
                .collect(toList());

        if (duplicateSectionNames.size() > CAN_DUPLICATE_MINIMUM_SIZE) {
            throw new SectionsHasDuplicateException(
                    String.format("중복 된 노선이 있습니다. 중복된 노선 목록 : %s"
                            , String.join(",", duplicateSectionNames)));
        }
    }

    private List<Section> validateAndSort(List<Section> sections) {
        Section firstSection = validateAndFindFirstSection(sections);
        return sortUpToDown(sections, firstSection);
    }

    private Section validateAndFindFirstSection(List<Section> sections) {
        Set<Station> upStations = toStationSet(sections, Section::getUpStation);
        Set<Station> downStations = toStationSet(sections, Section::getDownStation);

        removeDuplicateStation(upStations, downStations);

        checkSizeOfEndStation(upStations, downStations);

        Station firstUpStation = upStations.iterator().next();
        return findSectionByStation(sections, firstUpStation)
                .orElseThrow(() -> new SectionsIllegalArgumentException(
                        String.format("노선 목록에 해당 역을 가진 노선이 없습니다. 역 : %s", firstUpStation)));
    }

    private void removeDuplicateStation(Set<Station> upStations, Set<Station> downStations) {
        Set<Station> upStationsCopy = new HashSet<>(upStations);
        upStations.removeAll(downStations);
        downStations.removeAll(upStationsCopy);
    }

    private void checkSizeOfEndStation(Set<Station> upStations, Set<Station> downStations) {
        if (upStations.size() != END_TERMINAL_SIZE || downStations.size() != END_TERMINAL_SIZE) {
            throw new SectionsIllegalArgumentException(
                    String.format("종점의 숫자가 %d개가 아닙니다. 상행 종점 갯수 : %d, 하행 종점 갯수 : %d",
                            END_TERMINAL_SIZE, upStations.size(), downStations.size()));
        }
    }

    private Optional<Section> findSectionByStation(List<Section> sections, Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny();
    }

    private Set<Station> toStationSet(List<Section> sections, Function<Section, Station> kindOfStaion) {
        return sections.stream()
                .map(kindOfStaion)
                .collect(toSet());
    }

    private List<Section> sortUpToDown(List<Section> sections, Section beforeSection) {
        List<Section> sorted = new ArrayList<>();

        while (!sections.isEmpty()) {
            Station downStation = beforeSection.getDownStation();
            sections.remove(beforeSection);
            sorted.add(beforeSection);

            Optional<Section> sectionByStation = findSectionByStation(sections, downStation);
            if (sectionByStation.isPresent()) {
                beforeSection = sectionByStation.get();
            }
        }

        return sorted;
    }

    public OrderedSections addSection(Section section) {
        List<Section> upStationSequences = findSequentialSections(section.getUpStation());
        List<Section> downStationSequences = findSequentialSections(section.getDownStation());

        if (upStationSequences.isEmpty() && downStationSequences.isEmpty()) {
            throw new SectionNotSequentialException(
                    String.format("상행역과 하행역 모두 노선에 존재하지 않습니다. 상행역 : %s, 하행역 : %s", section.getUpStation(), section.getDownStation()));
        }

        if (bothNotEmpty(upStationSequences, downStationSequences)) {
            throw new SectionsHasDuplicateException(
                    String.format("상행역과 하행역이 이미 노선에 모두 존재합니다. 상행역 : %s, 하행역 : %s", section.getUpStation(), section.getDownStation()));
        }

        modifyAdjacentUpSection(section, upStationSequences);
        modifyAdjacentDownSection(section, downStationSequences);

        sections.add(section);
        return new OrderedSections(sections);
    }

    private void modifyAdjacentUpSection(Section section, List<Section> upStationSequences) {
        if (!upStationSequences.isEmpty()) {
            Optional<Section> adjacentSection = upStationSequences.stream()
                    .filter(s -> s.getUpStation().equals(section.getUpStation()))
                    .findAny();
            adjacentSection.ifPresent(adjacent -> {
                sections.remove(adjacent);
                sections.add(adjacent.updateUpStation(section.getDownStation(), section.getDistance()));
            });
        }
    }

    private void modifyAdjacentDownSection(Section section, List<Section> downStationSequences) {
        if (!downStationSequences.isEmpty()) {
            Optional<Section> adjacentSection = downStationSequences.stream()
                    .filter(s -> s.getDownStation().equals(section.getDownStation()))
                    .findAny();
            adjacentSection.ifPresent(adjacent -> {
                sections.remove(adjacent);
                sections.add(adjacent.updateDownStation(section.getUpStation(), section.getDistance()));
            });
        }
    }

    private boolean bothNotEmpty(List<Section> upStationSequences, List<Section> downStationSequences) {
        return !upStationSequences.isEmpty() && !downStationSequences.isEmpty();
    }

    private List<Section> findSequentialSections(Station station) {
        return sections.stream()
                .filter(section -> section.isExist(station))
                .collect(toList());
    }

    public OrderedSections removeSection(Station station) {
        List<Section> sequences = findSequentialSectionsAndCheckPresent(station);
        this.sections.removeAll(sequences);

        if (sequences.size() == BETWEEN_SECTION) {
            Section mergedSection = sequences.get(FIRST_ADJACENT_SECTION_INDEX)
                    .mergeWithSequentialSection(sequences.get(SECOND_ADJACENT_SECTION_INDEX));
            this.sections.add(mergedSection);
        }

        return new OrderedSections(this.sections);
    }

    private List<Section> findSequentialSectionsAndCheckPresent(Station station) {
        List<Section> sequentialSections = findSequentialSections(station);
        if (sequentialSections.isEmpty()) {
            throw new SectionsHasNotSectionException(
                    String.format("해당 구간에 삭제하려는 역이 없습니다. 역이름 : %s", station.toString()));
        }
        return sequentialSections;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Section> getReverseSections() {
        List<Section> copy = new LinkedList<>(this.sections);
        Collections.reverse(copy);
        return Collections.unmodifiableList(copy);
    }
}
