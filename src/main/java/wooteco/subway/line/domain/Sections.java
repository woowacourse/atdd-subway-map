package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {
    private static final int INITIAL_STATION_SIZE = 2;

    private final List<Section> sections;
    private final List<Station> orderedStations;

    public Sections(List<Section> sections) {
        this.sections = sections;
        orderedStations = orderedStations();
    }

    public List<Station> getOrderedStations() {
        return orderedStations;
    }

    private List<Station> orderedStations() {
        Map<Station, Station> stationMap = sections.stream().collect(Collectors.toMap(
                Section::getUpStation,
                Section::getDownStation
        ));
        Station firstUpStation = findFirstUpStation(stationMap);
        return orderStationsByFirstUpStation(stationMap, firstUpStation);
    }

    private List<Station> orderStationsByFirstUpStation(Map<Station, Station> sectionMap, Station upStation) {
        List<Station> stationsOfLine = new ArrayList<>();
        stationsOfLine.add(upStation);
        while (sectionMap.get(upStation) != null) {
            upStation = sectionMap.get(upStation);
            stationsOfLine.add(upStation);
        }
        return stationsOfLine;
    }

    private Station findFirstUpStation(Map<Station, Station> sectionMap) {
        Set<Station> upStations = new HashSet<>(sectionMap.keySet());
        Set<Station> downStations = new HashSet<>(sectionMap.values());
        upStations.removeAll(downStations);
        return upStations.iterator().next();
    }

    public Station findSameStationsOfSection(Long upStationId, Long downStationId) {
        List<Station> stations = orderedStations.stream()
                .filter(station -> station.isSame(upStationId) || station.isSame(downStationId))
                .collect(Collectors.toList());

        if (stations.size() != 1) {
            throw new IllegalArgumentException("구간은 하나의 역만 중복될 수 있습니다.");
        }

        return stations.get(0);
    }

    public Section findSelectedSection(boolean isUpStation, Section newSection) {
        Function<Section, Station> sectionFunction = findFunction(isUpStation);
        Long stationId = sectionFunction.apply(newSection).getId();

        Section selectedSection = selectedSection(isUpStation, stationId);
        validateDistance(selectedSection, newSection.getDistance());
        return selectedSection;
    }

    public Section selectedSection(boolean isUpStation, Long stationId) {
        Function<Section, Station> sectionFunction = findFunction(isUpStation);
        return sections.stream()
                .filter(section -> sectionFunction.apply(section).isSame(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("중복되는 역이 없습니다!!"));
    }

    private Function<Section, Station> findFunction(boolean isUpStation) {
        if (isUpStation) {
            return Section::getUpStation;
        }
        return Section::getDownStation;
    }

    private void validateDistance(Section selectSection, int distance) {
        if (selectSection.isLessOrSameDistance(distance)) {
            throw new IllegalArgumentException("거리가 현재 존재하는 구간보다 크거나 같습니다!");
        }
    }

    public void validDeletableSection() {
        if (orderedStations.size() == INITIAL_STATION_SIZE) {
            throw new IllegalArgumentException("종점역만 남은 경우 삭제를 수행할 수 없습니다!");
        }
    }

    public boolean notEndStation(Long stationId) {
        return !orderedStations.get(0).isSame(stationId) &&
                !orderedStations.get(orderedStations.size() - 1).isSame(stationId);
    }
}
