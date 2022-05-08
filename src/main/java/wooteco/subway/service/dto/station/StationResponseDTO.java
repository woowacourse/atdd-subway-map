package wooteco.subway.service.dto.station;

import wooteco.subway.domain.Station;

public class StationResponseDTO {
    private Long id;
    private String name;

    public StationResponseDTO() {
    }

    public StationResponseDTO(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
