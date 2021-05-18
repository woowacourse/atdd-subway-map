package wooteco.subway.station.dto;

import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static List<StationResponse> toDtos(final List<Station> stations) {
        return stations.stream()
                .map(StationResponse::toDto)
                .collect(Collectors.toList());
    }

    public static StationResponse toDto(final Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
