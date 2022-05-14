package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.ResultSetExtractor;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

class LineResponsesExtractor implements ResultSetExtractor<List<LineResponse>> {

    private final Set<Long> lineIds;
    private final Map<Long, String> lineNames;
    private final Map<Long, String> lineColors;
    private final Map<Long, StationResponse> stations;
    private final Map<Long, Map<Long, Long>> edges;

    LineResponsesExtractor() {
        lineIds = new HashSet<>();
        stations = new HashMap<>();
        lineNames = new HashMap<>();
        lineColors = new HashMap<>();
        edges = new HashMap<>();
    }

    @Override
    public List<LineResponse> extractData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            long lineId = rs.getLong("line_id");
            String lineName = rs.getString("line_name");
            String lineColor = rs.getString("line_color");

            long upStationId = rs.getLong("up_station_id");
            String upStationName = rs.getString("up_station_name");

            long downStationId = rs.getLong("down_station_id");
            String downStationName = rs.getString("down_station_name");

            lineIds.add(lineId);
            lineNames.put(lineId, lineName);
            lineColors.put(lineId, lineColor);

            stations.put(upStationId, new StationResponse(upStationId, upStationName));
            stations.put(downStationId, new StationResponse(downStationId, downStationName));

            if (!edges.containsKey(lineId)) {
                edges.put(lineId, new HashMap<>());
            }

            edges.get(lineId).put(upStationId, downStationId);
        }

        List<LineResponse> result = new ArrayList<>();

        for (Long lineId : lineIds) {
            List<StationResponse> stationResponses = mapToStationResponses(lineId);
            result.add(new LineResponse(lineId, lineNames.get(lineId), lineColors.get(lineId), stationResponses));
        }

        return result;
    }

    private List<StationResponse> mapToStationResponses(long lineId) {
        Map<Long, Long> edge = edges.get(lineId);
        List<StationResponse> result = new ArrayList<>();

        Long next = findLastUpStation(edge);

        while(next != null) {
            StationResponse station = stations.get(next);
            result.add(station);
            next = edge.get(next);
        }

        return result;
    }

    private Long findLastUpStation(Map<Long, Long> edge) {
        for (Long upStationId : edge.keySet()) {
            if (!edge.containsValue(upStationId)) {
                return upStationId;
            }
        }
        throw new IllegalStateException("상행 종점이 없습니다.");
    }
}
