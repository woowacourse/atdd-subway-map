package wooteco.subway.admin.dto;


import wooteco.subway.admin.domain.Station;

import javax.validation.constraints.NotEmpty;

public class StationCreateRequest {
    @NotEmpty
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
