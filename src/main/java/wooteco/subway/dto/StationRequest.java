package wooteco.subway.dto;

import wooteco.subway.domain.Station;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank
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
