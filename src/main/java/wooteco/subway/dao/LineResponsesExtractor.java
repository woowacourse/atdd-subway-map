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

    public static final String LINE_ID = "line_id";
    public static final String LINE_NAME = "line_name";
    public static final String LINE_COLOR = "line_color";
    public static final String UP_STATION_ID = "up_station_id";
    public static final String UP_STATION_NAME = "up_station_name";
    public static final String DOWN_STATION_ID = "down_station_id";
    public static final String DOWN_STATION_NAME = "down_station_name";

    private final StationResponseSorter sorter;
    private final Set<Long> lineIds;
    private final Map<Long, String> lineNames;
    private final Map<Long, String> lineColors;
    private final Map<Long, Map<StationResponse, StationResponse>> stationGraphs;

    LineResponsesExtractor(StationResponseSorter sorter) {
        this.sorter = sorter;
        this.lineIds = new HashSet<>();
        this.lineNames = new HashMap<>();
        this.lineColors = new HashMap<>();
        this.stationGraphs = new HashMap<>();
    }

    @Override
    public List<LineResponse> extractData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            storeLineData(rs.getLong(LINE_ID), rs.getString(LINE_NAME), rs.getString(LINE_COLOR));
            storeStationGraph(
                rs.getLong(LINE_ID), rs.getLong(UP_STATION_ID), rs.getString(UP_STATION_NAME),
                rs.getLong(DOWN_STATION_ID), rs.getString(DOWN_STATION_NAME));
        }

        return mapToLineResponses();
    }

    private void storeLineData(long lineId, String lineName, String lineColor) {
        lineIds.add(lineId);
        lineNames.put(lineId, lineName);
        lineColors.put(lineId, lineColor);
    }

    private void storeStationGraph(long lineId, long upStationId, String upStationName,
                                   long downStationId, String downStationName) {
        if (!stationGraphs.containsKey(lineId)) {
            stationGraphs.put(lineId, new HashMap<>());
        }
        Map<StationResponse, StationResponse> graph = stationGraphs.get(lineId);
        graph.put(new StationResponse(upStationId, upStationName),
            new StationResponse(downStationId, downStationName));
    }

    private List<LineResponse> mapToLineResponses() {
        return lineIds.stream()
            .map(this::createLineResponse)
            .collect(Collectors.toList());
    }

    private LineResponse createLineResponse(Long lineId) {
        return new LineResponse(lineId, lineNames.get(lineId), lineColors.get(lineId),
            sorter.getSortedStations(stationGraphs.get(lineId)));
    }
}
