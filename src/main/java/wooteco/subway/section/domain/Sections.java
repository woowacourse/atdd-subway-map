package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.NoSuchStationException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    public static final int MINIMUM_SECTION_SIZE = 1;

    private final List<Section> sections;

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getOrderedStations() {
        Map<Station, Station> upAndDownStations = toSectionMap();

        Station firstStation = getFirstStation(upAndDownStations);
        List<Station> stations = new ArrayList<>(Collections.singletonList(firstStation));

        for (int i = 0; i < upAndDownStations.size(); i++) {
            Station currentDownEndStation = stations.get(stations.size() - 1);
            Station nextDownStation = upAndDownStations.get(currentDownEndStation);
            stations.add(nextDownStation);
        }
        return stations;
    }

    private Map<Station, Station> toSectionMap() {
        return sections.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private Station getFirstStation(final Map<Station, Station> upAndDownStations) {
        return upAndDownStations.keySet()
                .stream()
                .filter(station -> !upAndDownStations.containsValue(station))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean bothStationsExist(final Section section) {
        List<Station> stations = getStations();
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    public boolean bothStationsDoNotExist(final Section section) {
        List<Station> stations = getStations();
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    private List<Station> getStations() {
        return sections.stream()
                .map(section -> Arrays.asList(section.getUpStation(), section.getDownStation()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Section findOriginalSection(final Section section) {
        if (doesExistInUpStation(section.getUpStation())) {
            return getOriginalSectionByUpStation(section);
        }
        return getOriginalSectionByDownStation(section);
    }

    private boolean doesExistInUpStation(final Station station) {
        return sections.stream().anyMatch(thisSection -> thisSection.hasUpStation(station));
    }

    private Section getOriginalSectionByUpStation(final Section section) {
        return sections.stream()
                .filter(thisSection -> thisSection.hasSameUpStation(section))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }

    private Section getOriginalSectionByDownStation(final Section section) {
        return sections.stream()
                .filter(thisSection -> thisSection.hasSameDownStation(section))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }

    public boolean isNotEndStationSave(final Section section) {
        return !((isEndStation(section.getDownStation()) && doesExistInUpStation(section.getDownStation())) ||
                (isEndStation(section.getUpStation()) && doesExistInDownStation(section.getUpStation())));
    }

    public boolean isEndStation(final Station station) {
        long count = sections.stream()
                .filter(thisSection -> thisSection.hasStation(station))
                .count();

        return count == 1;
    }

    private boolean doesExistInDownStation(final Station station) {
        return sections.stream().anyMatch(thisSection -> thisSection.hasDownStation(station));
    }

    public boolean doesStationExist(final Station station) {
        return sections.stream().anyMatch(section -> section.hasStation(station));
    }

    public boolean isUnableToDelete() {
        return sections.size() <= MINIMUM_SECTION_SIZE;
    }

    public Section createNewSection(final Long lineId, final Station stationToDelete) {
        Long newUpStationId = getNewUpStationId(stationToDelete);
        Long newDownStationId = getNewDownStationId(stationToDelete);
        Integer newDistance = getNewDistance(stationToDelete);

        return new Section(
                lineId,
                new Station(newUpStationId),
                new Station(newDownStationId),
                newDistance);
    }

    private Long getNewUpStationId(final Station station) {
        Section sectionWithNewUpStation = sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);

        return sectionWithNewUpStation.getUpStationId();
    }

    private Long getNewDownStationId(final Station station) {
        Section sectionWithNewDownStation = sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);

        return sectionWithNewDownStation.getDownStationId();
    }

    private Integer getNewDistance(final Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station) || section.hasDownStation(station))
                .mapToInt(Section::getDistance)
                .sum();
    }
}
