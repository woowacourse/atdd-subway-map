package wooteco.subway.dto;

import wooteco.subway.domain.station.Station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station createStation() {
        return new Station(this.name);
    }

    public String getName() {
        return name;
    }
}
