package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Station;

public class StationRequest {
    @NotBlank
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
