package wooteco.subway.station.controller.dto;

import wooteco.subway.station.domain.Station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(this.name);
    }
}
