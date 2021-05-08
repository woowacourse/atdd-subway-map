package wooteco.subway.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import wooteco.subway.domain.Station;

public class StationRequest {
    private final String name;

    @JsonCreator
    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(null, name);
    }
}
