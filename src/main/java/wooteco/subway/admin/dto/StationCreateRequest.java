package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {

    @NotBlank(message = "역명은 필수입력 항목입니다.")
    private String name;

    public StationCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
