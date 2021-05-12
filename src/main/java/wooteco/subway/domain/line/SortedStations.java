package wooteco.subway.domain.line;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.Section;
import wooteco.subway.web.dto.StationResponse;

public class SortedStations {

    Map<Long, Long> up = new HashMap<>();
    Map<Long, Long> down = new HashMap<>();
    ArrayDeque<Long> result = new ArrayDeque<>();

    private final List<Section> sections;
    private final Map<Long, StationResponse> stationMap;

    public SortedStations(List<Section> sections, List<StationResponse> stations) {
        this.sections = sections;
        this.stationMap = new HashMap<>();
        for (StationResponse station : stations) {
            stationMap.put(station.getId(), station);
        }
    }

    public List<StationResponse> get() {
        init(sections);
        sort();

        return result.stream()
                .map(stationMap::get)
                .collect(Collectors.toList());
    }

    private void init(List<Section> sections) {
        for (Section section : sections) {
            up.put(section.getDownStationId(), section.getUpStationId());
            down.put(section.getUpStationId(), section.getDownStationId());
        }

        result.addFirst(sections.get(0).getUpStationId());
    }

    private void sort() {
        while (up.containsKey(result.peekFirst())) {
            result.addFirst(up.get(result.peekFirst()));
        }
        while (down.containsKey(result.peekLast())) {
            result.addLast(down.get(result.peekLast()));
        }
    }
}
