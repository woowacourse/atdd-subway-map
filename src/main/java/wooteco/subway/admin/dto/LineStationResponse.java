package wooteco.subway.admin.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
    private Long stationId;
    private String stationsName;
    private Long preStationId;
    private String preStationName;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long stationId, String stationsName, Long preStationId, String preStationName,
            int distance, int duration) {
        this.stationId = stationId;
        this.stationsName = stationsName;
        this.preStationId = preStationId;
        this.preStationName = preStationName;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation lineStation) {
        return new LineStationResponse(lineStation.getStationId(), null, lineStation.getPreStationId(),
                null, lineStation.getDistance(), lineStation.getDuration());
    }

    public static List<LineStationResponse> listOf(Set<LineStation> stations) {
        return stations.stream()
                .map(LineStationResponse::of)
                .collect(Collectors.toList());
    }

    public Long getStationId() {
        return stationId;
    }

    public String getStationsName() {
        return stationsName;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
