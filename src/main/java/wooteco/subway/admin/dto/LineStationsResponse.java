package wooteco.subway.admin.dto;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineStationsResponse {
    private Long lineId;
    private String lineName;
    private List<LineStationResponse> lineStations;

    public LineStationsResponse() {
    }

    public LineStationsResponse(Long lineId, String lineName,
            List<LineStationResponse> lineStations) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineStations = lineStations;
    }

    public static LineStationsResponse of(Line line, List<Station> stations) {
        return new LineStationsResponse(line.getId(), line.getName(),
                LineStationResponse.listOf(line.getStations(), stations));
    }

    public static List<LineStationsResponse> listOf(List<Line> lines, List<Station> stations) {
        return lines.stream()
                .map(line -> LineStationsResponse.of(line, stations))
                .collect(Collectors.toList());
    }

    public Long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public List<LineStationResponse> getLineStations() {
        return lineStations;
    }
}
