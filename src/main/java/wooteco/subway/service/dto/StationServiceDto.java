package wooteco.subway.service.dto;

import javax.validation.constraints.NotEmpty;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.domain.Station;

public class StationServiceDto {

    private final Long id;
    @NotEmpty
    private final String name;

    public StationServiceDto(Long id) {
        this(id, null);
    }

    public StationServiceDto(String name) {
        this(null, name);
    }

    public StationServiceDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationServiceDto from(StationRequest stationRequest) {
        return new StationServiceDto(stationRequest.getName());
    }

    public static StationServiceDto from(Station station) {
        return new StationServiceDto(station.getId(), station.getName());
    }

    public Station toEntity() {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
