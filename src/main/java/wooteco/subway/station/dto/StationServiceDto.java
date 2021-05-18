package wooteco.subway.station.dto;

import javax.validation.constraints.NotEmpty;
import wooteco.subway.station.domain.Station;

public class StationServiceDto {

    private final Long id;
    @NotEmpty
    private final String name;

    public StationServiceDto(final Long id) {
        this(id, null);
    }

    public StationServiceDto(final String name) {
        this(null, name);
    }

    public StationServiceDto(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationServiceDto from(final StationRequest stationRequest) {
        return new StationServiceDto(stationRequest.getName());
    }

    public static StationServiceDto from(final Station station) {
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
