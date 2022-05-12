package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wooteco.subway.exception.CustomException;
import wooteco.subway.exception.RowDuplicatedException;

public class StationSeries {

    private final List<Station> stations;

    public StationSeries(List<Station> stations) {
        this.stations = stations;
    }

    public static StationSeries fromSectionsAsOrdered(List<Section> sections) {
        final Map<Station, Station> sectionMap = sections.stream()
            .collect(Collectors.toMap(
                Section::getUpStation,
                Section::getDownStation));
        Station cursor = findUpTerminal(sectionMap);
        return new StationSeries(getOrderedStations(sectionMap, cursor));
    }

    private static Station findUpTerminal(Map<Station, Station> sectionMap) {
        return sectionMap.keySet()
            .stream()
            .filter(upStation -> !sectionMap.containsValue(upStation))
            .findAny()
            .orElseThrow(() -> new CustomException("상행 종점을 찾을 수 없습니다.")); // fix to refined
    }

    private static List<Station> getOrderedStations(Map<Station, Station> sectionMap, Station cursor) {
        List<Station> orderedStations = new ArrayList<>();
        while (cursor != null)  {
            orderedStations.add(cursor);
            cursor = sectionMap.get(cursor);
        }
        return orderedStations;
    }

    public Station create(String name) {
        validateDistinct(name);
        return new Station(name);
    }

    private void validateDistinct(String name) {
        if (stations.stream().anyMatch(station -> station.getName().equals(name))) {
            throw new RowDuplicatedException(String.format("%s는 이미 존재하는 역 이름입니다.", name));
        }
    }

    public List<Station> getStations() {
        return List.copyOf(stations);
    }
}
