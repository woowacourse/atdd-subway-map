package wooteco.subway.admin.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
    @NotBlank(message = "역 이름을 작성해주세요!")
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
