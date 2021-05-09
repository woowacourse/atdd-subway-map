package wooteco.subway.dto.station.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class StationRequest {

    @NotBlank
    @Pattern(regexp = "^[가-힣|A-Z|a-z| 0-9]*역$")
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
