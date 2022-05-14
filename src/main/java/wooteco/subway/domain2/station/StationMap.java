package wooteco.subway.domain2.station;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.domain2.section.Section;

public class StationMap {

    private final Map<Long, Station> value;

    private StationMap(Map<Long, Station> value) {
        this.value = value;
    }

    public static StationMap of(List<Section> sections) {
        Map<Long, Station> value = toUniqueStationList(sections)
                .stream()
                .collect(Collectors.toMap(Station::getId, station -> station));
        return new StationMap(value);
    }

    private static Set<Station> toUniqueStationList(List<Section> sections) {
        Set<Station> stations = new HashSet<>();
        stations.addAll(extractUpStations(sections));
        stations.addAll(extractDownStations(sections));
        return stations;
    }

    private static List<Station> extractUpStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private static List<Station> extractDownStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

   public int getSize() {
        return value.size();
   }

   public Station findEntityOfId(Long stationId){
        return value.get(stationId);
   }
}
