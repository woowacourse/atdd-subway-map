package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.ResultSetExtractor;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

class LineResponsesExtractor implements ResultSetExtractor<List<LineResponse>> {

    private final Set<Long> lineIds;
    private final Map<Long, Set<StationResponse>> stations;
    private final Map<Long, String> names;
    private final Map<Long, String> colors;

    LineResponsesExtractor() {
        lineIds = new HashSet<>();
        stations = new HashMap<>();
        names = new HashMap<>();
        colors = new HashMap<>();
    }

    @Override
    public List<LineResponse> extractData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            long lineId = rs.getLong("line_id");
            String lineName = rs.getString("line_name");
            String lineColor = rs.getString("line_color");

            storeIdAndNameAndColor(lineId, lineName, lineColor);
            storeStationResponse(rs, lineId);
        }

        return mapToLinResponses();
    }

    private void storeIdAndNameAndColor(long lineId, String lineName, String lineColor) {
        lineIds.add(lineId);
        names.put(lineId, lineName);
        colors.put(lineId, lineColor);
    }

    private void storeStationResponse(ResultSet rs, long lineId) throws SQLException {
        if (!stations.containsKey(lineId)) {
            stations.put(lineId, new HashSet<>());
        }
        Set<StationResponse> stationResponses = stations.get(lineId);
        stationResponses.add(mapToStationResponse(rs));
    }

    private StationResponse mapToStationResponse(ResultSet rs) throws SQLException {
        long id = rs.getLong("station_id");
        String name = rs.getString("station_name");
        return new StationResponse(id, name);
    }

    private List<LineResponse> mapToLinResponses() {
        return lineIds.stream()
            .map(id -> new LineResponse(id, names.get(id), colors.get(id), stations.get(id)))
            .collect(Collectors.toList());
    }
}
