package wooteco.subway.station.controller;

import wooteco.subway.station.domain.Station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(Station station) {
        this.name = station.getName().text();
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
