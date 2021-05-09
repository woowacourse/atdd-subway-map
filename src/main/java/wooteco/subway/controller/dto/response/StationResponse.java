package wooteco.subway.controller.dto.response;

import javax.validation.constraints.NotEmpty;
import wooteco.subway.service.dto.StationServiceDto;

public class StationResponse {

    @NotEmpty
    private Long id;
    @NotEmpty
    private String name;

    public StationResponse() {
    }

    public StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(final StationServiceDto stationServiceDto) {
        return new StationResponse(stationServiceDto.getId(), stationServiceDto.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
