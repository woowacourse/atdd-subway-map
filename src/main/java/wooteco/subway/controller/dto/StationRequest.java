package wooteco.subway.controller.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wooteco.subway.domain.Station;

public class StationRequest {

    @NotBlank
    private final String name;

    @JsonCreator
    public StationRequest(@JsonProperty(value = "name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(null, name);
    }
}
