package wooteco.subway.station.dto;

import javax.validation.constraints.NotEmpty;

public class StationRequest {

    @NotEmpty
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
