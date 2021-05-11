package wooteco.subway.controller.request;

import wooteco.subway.domain.Station;

import javax.validation.constraints.NotNull;

public class StationRequest {
    @NotNull
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(name);
    }
}
