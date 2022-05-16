package wooteco.subway.dto.request;

import javax.validation.constraints.NotBlank;

public class CreateStationRequest {

    @NotBlank
    private String name;

    private CreateStationRequest() {
    }

    public CreateStationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
