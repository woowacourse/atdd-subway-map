package wooteco.subway.station.controller;

import wooteco.subway.station.domain.Station;

import javax.validation.constraints.NotEmpty;

public class StationRequest {
    @NotEmpty
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
