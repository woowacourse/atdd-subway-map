package wooteco.subway.dto;

import wooteco.subway.utils.ExceptionMessage;

import javax.validation.constraints.NotEmpty;

public class StationRequest {
    @NotEmpty(message = ExceptionMessage.NO_NAME_BLANK)
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
