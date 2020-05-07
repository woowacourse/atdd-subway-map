package wooteco.subway.admin.dto.res;

public class LineStationResponse {
    private StationResponse preStation;
    private StationResponse station;
    private int distance;
    private int duration;
    private LineResponse lineResponse;

    public LineStationResponse(StationResponse preStation, StationResponse station, int distance,
        int duration, LineResponse lineResponse) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
        this.duration = duration;
        this.lineResponse = lineResponse;
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
