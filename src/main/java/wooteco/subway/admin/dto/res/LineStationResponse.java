package wooteco.subway.admin.dto.res;

public class LineStationResponse {
    private Long id;
    private StationResponse preStation;
    private StationResponse station;
    private int distance;
    private int duration;
    private LineResponse lineResponse;

    public LineStationResponse(Long id, StationResponse preStation,
        StationResponse station, int distance, int duration,
        LineResponse lineResponse) {
        this.id = id;
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
        this.duration = duration;
        this.lineResponse = lineResponse;
    }

    public Long getId() {
        return id;
    }

    public StationResponse getPreStation() {
        return preStation;
    }

    public StationResponse getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public LineResponse getLineResponse() {
        return lineResponse;
    }
}
