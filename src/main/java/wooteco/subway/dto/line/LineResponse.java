package wooteco.subway.dto.line;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import wooteco.subway.domain.line.Line;
import wooteco.subway.dto.station.StationResponse;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    private LineResponse(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), convertStationResponses(line));
    }

    public static LineResponse from(Line line) {
        if (Objects.isNull(line.getStations())) {
            return new LineResponse(line.getId(), line.getName(), line.getColor());
        }
        return new LineResponse(line.getId(), line.getName(), line.getColor(), convertStationResponses(line));
    }

    private static List<StationResponse> convertStationResponses(Line line) {
        return line.getStations().stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
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
