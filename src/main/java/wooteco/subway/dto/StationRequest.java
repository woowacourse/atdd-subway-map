package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Station;

public class StationRequest {

    @NotBlank(message = "station 이름은 공백 혹은 null이 들어올 수 없습니다.")
    private String name;

    private StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public Station toStation() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
