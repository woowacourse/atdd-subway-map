package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

public class LineStationCreateRequest {
    private String lineName;

    private Long preStationId;
    private String preStationName;
    private Long stationId;
    private String stationName;
    private int distance;

    public LineStationCreateRequest(String lineName, Long preStationId, String preStationName, Long stationId, String stationName, int distance, int duration) {
        this.lineName = lineName;
        this.preStationId = preStationId;
        this.preStationName = preStationName;
        this.stationId = stationId;
        this.stationName = stationName;
        this.distance = distance;
        this.duration = duration;
    }

    private int duration;
    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
        this(null, preStationId, null, stationId, null, distance, duration);
    }

    public static LineStationCreateRequest of(Line line, Station preStation, Station station, int distance, int duration) {
        return new LineStationCreateRequest(line.getName(), preStation.getId(), preStation.getName(), station.getId(), station.getName(), distance, duration);
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

    public String getLineName() {
        return lineName;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }
}
