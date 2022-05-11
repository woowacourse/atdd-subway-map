package wooteco.subway.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getOrderedStations() {
        Map<Station, Station> stationRoute = new HashMap<>();
        for (Section section : this.sections) {
            stationRoute.put(section.getUpStation(), section.getDownStation());
        }
        Station firstUpStation = getFirstUpStation(stationRoute);

        return orderStations(stationRoute, firstUpStation);
    }

    private Station getFirstUpStation(Map<Station, Station> stationRoute) {
        return stationRoute.keySet().stream()
                .filter(station -> isFirstUpStation(stationRoute, station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("현재 상행 종점이 없습니다."));
    }

    private boolean isFirstUpStation(Map<Station, Station> stationRoute, Station station) {
        List<Station> asd = stationRoute.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), station))
                .map(Entry::getKey)
                .collect(Collectors.toList());
        return asd.size() == 0;
    }

    private List<Station> orderStations(Map<Station, Station> stationRoute, Station firstUpStation) {
        List<Station> orderedStations = new LinkedList<>();
        orderedStations.add(firstUpStation);

        Station station = firstUpStation;
        while (stationRoute.containsKey(station)) {
            station = stationRoute.get(station);
            orderedStations.add(station);
        }
        return orderedStations;
    }

    public Optional<Section> findSectionByAddingSection(Station upStation, Station downStation, int distance) {
        if (this.sections.size() == 0) {
            return Optional.empty();
        }
        List<Station> orderedStations = getOrderedStations();
        checkStationExist(upStation, downStation, orderedStations);
        checkSectionAlreadyExist(upStation, downStation, orderedStations);
        Optional<Section> wrappedSection = findNeedToUpdateSection(upStation, downStation, orderedStations);
        wrappedSection.ifPresent(section -> checkAlreadyExistSectionDistance(section, distance));
        return wrappedSection;
    }

    private void checkStationExist(Station upStation, Station downStation, List<Station> orderedStations) {
        if (!orderedStations.contains(upStation) && !orderedStations.contains(downStation)) {
            throw new IllegalArgumentException("입력한 상행과 하행 중 노선에 등록된 지하철이 없습니다.");
        }
    }

    private void checkSectionAlreadyExist(Station upStation, Station downStation, List<Station> orderedStations) {
        if (orderedStations.contains(upStation) && orderedStations.contains(downStation)) {
            throw new IllegalArgumentException("입력한 상행 하행 구간이 이미 연결되어 있는 구간입니다.");
        }
    }

    private Optional<Section> findNeedToUpdateSection(Station upStation, Station downStation,
                                                      List<Station> orderedStations) {
        if (orderedStations.contains(upStation)) {
            int nextIndex = orderedStations.indexOf(upStation) + 1;
            if (nextIndex >= orderedStations.size()) {
                return Optional.empty();
            }
            return Optional.of(getNeedToUpdateSection(upStation, orderedStations.get(nextIndex)));
        }
        int beforeIndex = orderedStations.indexOf(downStation) - 1;
        if (beforeIndex < 0) {
            return Optional.empty();
        }
        return Optional.of(getNeedToUpdateSection(orderedStations.get(beforeIndex), downStation));
    }

    private Section getNeedToUpdateSection(Station upStation, Station downStation) {
        return sections.stream()
                .filter(section -> section.matchUpStationAndDownStation(upStation, downStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 상행 지하철역이 없습니다."));
    }

    private void checkAlreadyExistSectionDistance(Section section, int distance) {
        section.checkDistance(distance);
    }

    public Station findNewStation(Station upStation, Station downStation) {
        long upStationMatchCount = this.sections.stream()
                .filter(section -> section.hasStation(upStation))
                .count();

        if (upStationMatchCount >= 1) {
            return downStation;
        }
        return upStation;
    }
}
