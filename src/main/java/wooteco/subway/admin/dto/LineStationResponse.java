package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineStationResponse {

    private Long id;
    private Long preStationId;
    private Long stationId;
    private int distance;
    private int duration;

    public LineStationResponse(Long id, Long preStationId, Long stationId, int distance, int duration) {
        this.id = id;
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getId(), lineStation.getPreStationId(), lineStation.getStationId(),
                lineStation.getDistance(), lineStation.getDuration());
    }

    public static List<LineStationResponse> listOf(List<LineStation> lineStations) {
        return lineStations.stream()
                .map(LineStationResponse::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
