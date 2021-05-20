package wooteco.subway.station.dto;

import wooteco.subway.station.Station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(this.name);
    }

    public String getName() {
        return name;
    }
}
