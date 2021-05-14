package wooteco.subway.domain.line;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wooteco.subway.domain.section.Section;
import wooteco.subway.web.dto.StationResponse;

public class SortedStations {

    private final Map<Long, Long> downToUp = new HashMap<>();
    private final Map<Long, Long> upToDown = new HashMap<>();

    private final List<Section> sections;
    private final Map<Long, StationResponse> stationMap = new HashMap<>();

    public SortedStations(List<Section> sections, List<StationResponse> stations) {
        this.sections = sections;

        for (StationResponse station : stations) {
            stationMap.put(station.getId(), station);
        }
    }

    public List<StationResponse> get() {
        init(sections);
        Deque<Long> sortedIds = sort();
        return idsToStationResponses(sortedIds);
    }

    private void init(List<Section> sections) {
        for (Section section : sections) {
            downToUp.put(section.getDownStationId(), section.getUpStationId());
            upToDown.put(section.getUpStationId(), section.getDownStationId());
        }
    }

    private Deque<Long> sort() {
        Deque<Long> result = new ArrayDeque<>();
        result.addFirst(sections.get(0).getUpStationId());

        while (downToUp.containsKey(result.peekFirst())) {
            result.addFirst(downToUp.get(result.peekFirst()));
        }
        while (upToDown.containsKey(result.peekLast())) {
            result.addLast(upToDown.get(result.peekLast()));
        }

        return result;
    }

    private List<StationResponse> idsToStationResponses(Deque<Long> result) {
        return result.stream()
                .map(stationMap::get)
                .collect(Collectors.toList());
    }
}
