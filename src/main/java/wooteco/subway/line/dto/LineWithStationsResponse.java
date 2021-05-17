package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineWithStationsResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineWithStationsResponse() {
    }

    public LineWithStationsResponse(Line line, List<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stationResponses(stations);
    }

    private List<StationResponse> stationResponses(List<Station> stations) {
        return stations
            .stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
