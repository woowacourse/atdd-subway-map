package wooteco.subway.dto;

import lombok.Getter;
import wooteco.subway.domain.Station;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Station station) {
        id = station.getId();
        name = station.getName();
    }
}
