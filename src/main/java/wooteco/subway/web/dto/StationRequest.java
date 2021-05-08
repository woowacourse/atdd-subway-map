package wooteco.subway.web.dto;

import wooteco.subway.domain.station.Station;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
