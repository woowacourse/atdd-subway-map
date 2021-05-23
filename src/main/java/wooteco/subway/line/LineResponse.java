package wooteco.subway.line;

import wooteco.subway.station.StationResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, Collections.emptyList());
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(),
                line.getStations().stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList()));
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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
