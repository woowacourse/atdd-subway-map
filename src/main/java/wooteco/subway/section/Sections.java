package wooteco.subway.section;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import wooteco.subway.exception.InvalidSectionDistanceException;
import wooteco.subway.exception.InvalidStationException;
import wooteco.subway.exception.NoneOrAllStationsExistingInLineException;
import wooteco.subway.station.Station;

public class Sections {

    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections(List<Section> sections, Section section) {
        this.sections = sections;
        validateSectionStations(section);
        validateSectionDistance(section);
    }

    private void validateSectionStations(Section newSection) {
        List<Station> stations = sections.stream()
            .map(section -> Arrays.asList(section.getUpStation(), section.getDownStation()))
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());

        List<Station> newSectionStations = Arrays.asList(newSection.getUpStation(),
            newSection.getDownStation());

        stations.retainAll(newSectionStations);
        if (stations.size() != 1) {
            throw new NoneOrAllStationsExistingInLineException();
        }
    }

    private void validateSectionDistance(Section newSection) {
        int distance = sectionToBeDivided(newSection).getDistance();
        if (newSection.largerThan(distance)) {
            throw new InvalidSectionDistanceException();
        }
    }

    public Section sectionToBeDivided(Section newSection) {
        return sections.stream()
            .filter(section -> hasSameUpOrDownStationId(newSection, section))
            .findAny()
            .orElseThrow(InvalidStationException::new);
    }

    public List<Station> sortedStations() {
        Deque<Station> sortedStations = new ArrayDeque<>();
        Map<Station, Station> upIds = new LinkedHashMap<>();
        Map<Station, Station> downIds = new LinkedHashMap<>();

        initializeByIds(sortedStations, upIds, downIds);
        sort(sortedStations, upIds, downIds);

        return new ArrayList<>(sortedStations);
    }

    public Section divideSection(Section newSection) {
        Section existingSection = sectionToBeDivided(newSection);
        if (existingSection.isUpStation(newSection.getUpStation())) {
            return new Section(existingSection.getId(), newSection.getDownStation(),
                existingSection.getDownStation(), existingSection.deductDistance(newSection));
        }
        return new Section(existingSection.getId(), existingSection.getUpStation(), newSection.getUpStation(),
            existingSection.deductDistance(newSection));
    }

    public boolean isNotEndPoint() {
        return sections.size() == 2;
    }

    public Station findDownStation(Long stationId) {
        return findStation(section -> section.getDownStation().getId().equals(stationId));
    }

    public Station findUpStation(Long stationId) {
        return findStation(section -> section.getUpStation().getId().equals(stationId));
    }

    public int sumDistance() {
        return sections.stream()
            .mapToInt(Section::getDistance)
            .sum();
    }

    private boolean hasSameUpOrDownStationId(Section newSection, Section section) {
        return section.getUpStation().equals(newSection.getUpStation()) || section
            .getDownStation().equals(newSection.getDownStation());
    }


    private void initializeByIds(Deque<Station> sortedIds, Map<Station, Station> upIds,
        Map<Station, Station> downIds) {
        for (Section section : sections) {
            upIds.put(section.getDownStation(), section.getUpStation());
            downIds.put(section.getUpStation(), section.getDownStation());
        }
        Section now = sections.get(0);
        sortedIds.addFirst(now.getUpStation());
    }

    private void sort(
        Deque<Station> sortedIds, Map<Station, Station> upIds, Map<Station, Station> downIds) {
        while (upIds.containsKey(sortedIds.peekFirst())) {
            Station current = sortedIds.peekFirst();
            sortedIds.addFirst(upIds.get(current));
        }
        while (downIds.containsKey(sortedIds.peekLast())) {
            Station current = sortedIds.peekLast();
            sortedIds.addLast(downIds.get(current));
        }
    }

    private Station findStation(Predicate<Section> predicate) {
        return sections.stream()
            .filter(predicate)
            .findAny()
            .orElseThrow(InvalidStationException::new)
            .getUpStation()
            ;
    }
}
