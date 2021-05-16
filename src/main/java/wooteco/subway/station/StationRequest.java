package wooteco.subway.station;

import javax.validation.constraints.Pattern;

public class StationRequest {
    @Pattern(regexp = ".*역$", message = "역 이름은 ~역으로 끝나야 합니다.")
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
