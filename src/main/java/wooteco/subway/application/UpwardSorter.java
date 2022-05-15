package wooteco.subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import wooteco.subway.dao.StationResponseSorter;
import wooteco.subway.dto.StationResponse;

public class UpwardSorter implements StationResponseSorter {

    @Override
    public List<StationResponse> getSortedStations(Map<StationResponse, StationResponse> graph) {
        List<StationResponse> result = new ArrayList<>();
        StationResponse current = findLastUpStation(graph);

        while (current != null) {
            result.add(current);
            current = graph.get(current);
        }

        return result;
    }

    private StationResponse findLastUpStation(Map<StationResponse, StationResponse> graph) {
        return graph.keySet().stream()
            .filter(Predicate.not(graph::containsValue))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("상행 종점이 없습니다."));
    }
}
