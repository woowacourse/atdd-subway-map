package wooteco.subway.controller.dto.request;

import wooteco.subway.domain.station.Station;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toDomain() {
        return new Station(name);
    }
}
