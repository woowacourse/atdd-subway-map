package wooteco.subway.admin.dto;

import javax.validation.constraints.NotEmpty;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
    @NotEmpty(message = "역 이름이 입력되지 않았습니다.")
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
