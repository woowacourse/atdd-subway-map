package wooteco.subway.line.controller.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
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
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
    }

    public static LineResponse from(Line line, List<Station> stations) {
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
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
