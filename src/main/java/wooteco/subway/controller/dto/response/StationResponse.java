package wooteco.subway.controller.dto.response;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import wooteco.subway.service.dto.StationServiceDto;

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

    public static StationResponse from(StationServiceDto stationServiceDto) {
        return new StationResponse(stationServiceDto.getId(), stationServiceDto.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
