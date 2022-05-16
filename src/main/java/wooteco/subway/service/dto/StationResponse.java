package wooteco.subway.service.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import wooteco.subway.domain.Station;

public class StationResponse {

    @NotNull
    private Long id;
    @NotEmpty
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
