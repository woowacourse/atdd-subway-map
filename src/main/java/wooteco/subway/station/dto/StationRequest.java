package wooteco.subway.station.dto;

public class StationRequest {
    private String name;

    private StationRequest() {
    }

    private StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
