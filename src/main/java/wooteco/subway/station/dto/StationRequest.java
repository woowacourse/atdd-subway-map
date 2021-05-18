package wooteco.subway.station.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank
    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
