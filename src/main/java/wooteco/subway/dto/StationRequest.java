package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank
    private String name;

    private StationRequest() {
    }

    public String getName() {
        return name;
    }
}
