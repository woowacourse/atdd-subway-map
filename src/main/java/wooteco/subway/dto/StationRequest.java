package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank(message = "역의 이름은 필수로 입력하여야 합니다.")
    private String name;

    private StationRequest() {
    }

    public String getName() {
        return name;
    }
}
