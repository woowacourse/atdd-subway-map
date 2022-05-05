package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Station;

public class StationRequest {

    @NotBlank(message = "역 이름은 빈 값일 수 없습니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }

}
