package wooteco.subway.admin.station.service.dto;


import wooteco.subway.admin.station.domain.Station;

import javax.validation.constraints.NotNull;

public class StationCreateRequest {
    @NotNull(message = "역 이름이 비어있습니다.")
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
