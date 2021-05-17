package wooteco.subway.section;

import wooteco.subway.section.exception.SectionInclusionException;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public boolean attachesAfterEndStation(SectionDto sectionDto) {
        List<Station> stations = sortedStations();
        Station firstStation = stations.get(0);
        Station lastStation = stations.get(stations.size() - 1);

        return (firstStation.isSameId(sectionDto.getDownStationId())
                && !stations.contains(new Station(sectionDto.getUpStationId(), "temp")))
                || (lastStation.isSameId(sectionDto.getUpStationId())
                        && !stations.contains(new Station(sectionDto.getDownStationId(), "temp")));
    }

    public void validateSectionInclusion(SectionDto sectionDto) {
        if (hasBothStations(sectionDto) || hasNeitherStations(sectionDto)) {
            throw new SectionInclusionException();
        }
    }

    private boolean hasBothStations(SectionDto sectionDto) {
        return sections.stream()
                .anyMatch(
                        section -> section.isSameUpStation(sectionDto.getUpStationId()))
                &&
                sections.stream()
                        .anyMatch(
                                section -> section.isSameDownStation(sectionDto.getDownStationId()));
    }

    private boolean hasNeitherStations(SectionDto sectionDto) {
        return sections.stream()
                .noneMatch(
                        section -> section.isSameUpStation(section.getUpStationId()))
                &&
                sections.stream()
                        .noneMatch(
                                section -> section.isSameDownStation(sectionDto.getDownStationId()));
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> sortedStations() {
        Deque<Station> sortedStations = new ArrayDeque<>();
        Map<Station, Station> upStations = new LinkedHashMap<>();
        Map<Station, Station> downStations = new LinkedHashMap<>();

        initializeByStations(sortedStations, upStations, downStations);
        sortByStations(sortedStations, upStations, downStations);

        return new ArrayList<>(sortedStations);
    }

    private void initializeByStations(Deque<Station> sortedStations, Map<Station, Station> upStations,
                                      Map<Station, Station> downStations) {
        for (Section section : sections) {
            upStations.put(section.getUpStation(), section.getDownStation());
            downStations.put(section.getDownStation(), section.getUpStation());
        }

        Section curSection = sections.get(0);
        sortedStations.addFirst(curSection.getUpStation());
        sortedStations.addLast(curSection.getDownStation());
    }

    private void sortByStations(Deque<Station> sortedStations, Map<Station, Station> upStations,
                                Map<Station, Station> downStations) {
        while (downStations.containsKey(sortedStations.peekFirst())) {
            Station station = sortedStations.peekFirst();
            sortedStations.addFirst(downStations.get(station));
        }
        while (upStations.containsKey(sortedStations.peekLast())) {
            Station station = sortedStations.peekLast();
            sortedStations.addLast(upStations.get(station));
        }
    }
}
