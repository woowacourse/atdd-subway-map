package wooteco.subway.dto;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor());
    }

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public LineResponse(Line line, List<StationResponse> stationResponses) {
        this(line.getId(), line.getName(), line.getColor(), stationResponses);
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
