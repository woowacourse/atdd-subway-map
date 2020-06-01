package wooteco.subway.admin.dto;

public class StationCreateRequest {
    private String name;
    private String preStationName;
    private int distance;
    private int duration;


    public String getPreStationName() {
        return preStationName;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }
}
