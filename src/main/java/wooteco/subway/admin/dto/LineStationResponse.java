package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.LineStation;

public class LineStationResponse {
    private Long lineId;
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public LineStationResponse() {
    }

    public LineStationResponse(Long lineId, Long stationId, Long preStationId, int distance, int duration) {
        this.lineId = lineId;
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(Long lineId, LineStation lineStation) {
        return new LineStationResponse(lineId, lineStation.getStationId(),
                lineStation.getPreStationId(), lineStation.getDistance(), lineStation.getDuration());
    }

    public static LineStationResponse of(Long lineId, LineStationCreateRequest LineStationCreateRequest) {
        return new LineStationResponse(lineId, LineStationCreateRequest.getStationId(),
                LineStationCreateRequest.getPreStationId(), LineStationCreateRequest.getDistance(),
                LineStationCreateRequest.getDuration());
    }

    public Long getLineId() {
        return lineId;
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
