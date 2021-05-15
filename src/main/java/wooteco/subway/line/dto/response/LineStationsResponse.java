package wooteco.subway.line.dto.response;

import wooteco.subway.line.Line;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineStationsResponse {
    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineStationsResponse() {
    }

    public LineStationsResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(),
                line.stations().stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList()));
    }

    public LineStationsResponse(long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public long getId() {
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
