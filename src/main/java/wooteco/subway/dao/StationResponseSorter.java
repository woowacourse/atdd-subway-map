package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import wooteco.subway.dto.StationResponse;

public interface StationResponseSorter {

    List<StationResponse> getSortedStations(Map<StationResponse, StationResponse> graph);
}
