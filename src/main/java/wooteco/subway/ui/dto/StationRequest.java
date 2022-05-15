package wooteco.subway.ui.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.service.dto.StationServiceRequest;

public class StationRequest {

    @NotBlank(message = "name을 입력해주세요.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public StationServiceRequest toServiceRequest() {
        return new StationServiceRequest(name);
    }
}
