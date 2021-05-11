package wooteco.subway.dto;

import java.util.List;

public class SectionResponse {

    private List<StationResponse> stations;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
