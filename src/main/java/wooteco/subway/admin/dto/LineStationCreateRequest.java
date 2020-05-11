package wooteco.subway.admin.dto;

public class LineStationCreateRequest {
    private String preStationName;
    private String stationName;
    private int distance;
    private int duration;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(String preStationName, String stationName,
                                    int distance, int duration) {
        this.preStationName = preStationName;
        this.stationName = stationName;
        this.distance = distance;
        this.duration = duration;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "LineStationCreateRequest{" +
                "preStationName='" + preStationName + '\'' +
                ", stationName='" + stationName + '\'' +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }
}
