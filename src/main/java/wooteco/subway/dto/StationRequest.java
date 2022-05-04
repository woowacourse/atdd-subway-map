package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank
    private String name;

    private StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
