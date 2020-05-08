package wooteco.subway.admin.dto;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.Line;

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

    public static LineStationsResponse of(Line line) {
        return new LineStationsResponse(line.getId(), line.getName(), LineStationResponse.listOf(line.getStations()));
    }

    public static List<LineStationsResponse> listOf(List<Line> lines) {
        return lines.stream()
                .map(LineStationsResponse::of)
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
