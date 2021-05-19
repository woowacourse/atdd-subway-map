package wooteco.subway.station;

import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank(message = "이름이 입력되지 않았거나 공백입니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
