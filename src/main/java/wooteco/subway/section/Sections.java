package wooteco.subway.section;

import wooteco.subway.section.exception.SectionCantDeleteException;
import wooteco.subway.section.exception.SectionInclusionException;
import wooteco.subway.section.exception.SectionNotFoundException;
import wooteco.subway.station.Station;
import wooteco.subway.station.exception.StationNotFoundException;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public boolean canAttachAfterEndStation(Long upStationId, Long downStationId) {
        List<Station> stations = sortedStations();
        Station firstStation = stations.get(0);
        Station lastStation = stations.get(stations.size() - 1);

        return (firstStation.isSameId(downStationId) && !stations.contains(new Station(upStationId, "temp")))
                || (lastStation.isSameId(upStationId) && !stations.contains(new Station(downStationId, "temp")));
    }

    public SectionStandard calculateSectionStandard(SectionDto sectionDto) {
        boolean condition = sections.stream()
                .anyMatch(section -> section.isSameUpStation(sectionDto.getUpStationId()));

        if (condition) {
            return SectionStandard.FROM_UP_STATION;
        }
        return SectionStandard.FROM_DOWN_STATION;
    }

    public void validateSectionInclusion(SectionDto sectionDto) {
        if (hasBothStations(sectionDto) || hasNeitherStations(sectionDto)) {
            throw new SectionInclusionException();
        }
    }

    private boolean hasBothStations(SectionDto sectionDto) {
        return sections.stream()
                .anyMatch(
                        section -> section.isSameUpStation(sectionDto.getUpStationId())
                                || section.isSameDownStation(sectionDto.getUpStationId()))
                &&
                sections.stream()
                        .anyMatch(
                                section -> section.isSameUpStation(sectionDto.getDownStationId())
                                        || section.isSameDownStation(sectionDto.getDownStationId())
                        );
    }

    private boolean hasNeitherStations(SectionDto sectionDto) {
        return sections.stream()
                .noneMatch(
                        section -> section.isSameUpStation(sectionDto.getUpStationId())
                                || section.isSameDownStation(sectionDto.getUpStationId())
                                || section.isSameUpStation(sectionDto.getDownStationId())
                                || section.isSameDownStation(sectionDto.getDownStationId()));
    }

    public void validateNumberOfStation() {
        if (sections.size() <= 1) {
            throw new SectionCantDeleteException();
        }
    }

    public boolean isEndStation(Long stationId) {
        List<Station> stations = sortedStations();
        Station tempStation = new Station(stationId, "temp");

        if (!stations.contains(tempStation)) {
            throw new StationNotFoundException();
        }
        return stations.get(0).equals(tempStation) || stations.get(stations.size() - 1).equals(tempStation);
    }

    public Section findSectionByDownStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(stationId))
                .findAny()
                .orElseThrow(SectionNotFoundException::new);
    }

    public Section findSectionByUpStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(stationId))
                .findAny()
                .orElseThrow(SectionNotFoundException::new);
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

