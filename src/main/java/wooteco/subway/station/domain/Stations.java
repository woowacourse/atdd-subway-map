package wooteco.subway.station.domain;

import wooteco.subway.line.domain.Section;
import wooteco.subway.station.dto.StationResponse;

import java.util.*;

public class Stations {
    private final List<StationResponse> stations;

    public Stations(final List<StationResponse> stations) {
        this.stations = stations;
    }

    public List<StationResponse> getOrderedStationResponses(final List<Section> sections){
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Long stationId : getSortedStationIds(sections)) {
            stationResponses.add(stations.stream()
                    .filter(stationResponse -> stationResponse.getId().equals(stationId))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new));
        }
        return stationResponses;
    }

    private List<Long> getSortedStationIds(final List<Section> sections) {
        Map<Long, Long> connectMap = new HashMap<>();
        for (Section section : sections) {
            connectMap.put(section.getUpStationId(), section.getDownStationId());
        }
        Long curId = getFrontId(connectMap);
        return sortStationIds(connectMap, curId);
    }

    private Long getFrontId(final Map<Long, Long> connectMap) {
        List<Long> keys = new ArrayList<>(connectMap.keySet());
        List<Long> values = new ArrayList<>(connectMap.values());
        return keys.stream().filter(key -> !values.contains(key)).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private List<Long> sortStationIds(final Map<Long, Long> connectMap, Long curId) {
        List<Long> stationIdsByLineId = new ArrayList<>();
        stationIdsByLineId.add(curId);
        while (connectMap.containsKey(curId)) {
            curId = connectMap.get(curId);
            stationIdsByLineId.add(curId);
        }
        return stationIdsByLineId;
    }
}
