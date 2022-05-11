package wooteco.subway.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this(id, name, color);
        this.stations = stations;
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static LineResponse of(Line line, List<Station> allStations, List<Long> stationIds) {
        List<Station> stationsOfLine = allStations.stream()
                .filter(station -> stationIds.contains(station.getId()))
                .collect(Collectors.toList());

        return LineResponse.of(line, stationsOfLine);
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return from(line).stations(stations);
    }

    private LineResponse stations(List<Station> stations) {
        this.stations = stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return this;
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
