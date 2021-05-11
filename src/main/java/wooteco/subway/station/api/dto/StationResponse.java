package wooteco.subway.station.api.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.model.Station;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
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
