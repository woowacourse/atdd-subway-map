package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank(message = "역 이름은 빈 값일 수 없습니다.")
    private final String name;

    private StationRequest() {
        this(null);
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
