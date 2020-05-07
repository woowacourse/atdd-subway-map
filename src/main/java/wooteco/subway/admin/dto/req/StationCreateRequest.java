package wooteco.subway.admin.dto.req;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
