package wooteco.subway.station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.dto.StationResponse;

public class Stations {

    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public void sort(Sections sections) {
        Map<Long, Station> numberToStation = getNumberToStation();
        stations.clear();

        List<Section> sortSections = sections.getSections();
        Long upStationId = sortSections.get(0).getUpStationId();

        stations.add(numberToStation.get(upStationId));
        for (Section section : sections.getSections()) {
            stations.add(numberToStation.get(section.getDownStationId()));
        }
    }

    private Map<Long, Station> getNumberToStation() {
        Map<Long, Station> numberToStation = new HashMap<>();
        for (Station station : stations) {
            numberToStation.put(station.getId(), station);
        }
        return numberToStation;
    }

    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    public Stream<Station> stream() {
        return stations.stream();
    }
}
