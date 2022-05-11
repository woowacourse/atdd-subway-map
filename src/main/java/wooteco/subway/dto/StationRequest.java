package wooteco.subway.dto;

import wooteco.subway.domain.Station;
import javax.validation.constraints.NotBlank;

public class StationRequest {

    @NotBlank(message = "역 이름은 공백일 수 없습니다.")
    private String name;

    private StationRequest() {
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
