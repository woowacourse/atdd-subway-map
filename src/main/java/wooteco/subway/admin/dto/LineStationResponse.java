package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LineStationResponse {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStationResponse() {

    }

    public LineStationResponse(Long stationId, Long preStationId, int distance, int duration) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static List<LineStationResponse> of(Set<LineStation> lineStations) {
        return lineStations.stream()
                .map(it -> LineStationResponse.of(it))
                .collect(Collectors.toList());
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getStationId(), lineStation.getPreStationId(),
                lineStation.getDistance(), lineStation.getDistance());
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
