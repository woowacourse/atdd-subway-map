package wooteco.subway.admin.dto;


import wooteco.subway.admin.domain.Station;

import javax.validation.constraints.NotBlank;

public class StationCreateRequest {
    @NotBlank(message = "역 이름이 비어있습니다.")
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
