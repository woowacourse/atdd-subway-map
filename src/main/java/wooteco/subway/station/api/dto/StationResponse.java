package wooteco.subway.station.api.dto;

import wooteco.subway.station.model.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationResponse {

    private Long id;
    private String name;

    private StationResponse() {

    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public static List<StationResponse> listOf(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
