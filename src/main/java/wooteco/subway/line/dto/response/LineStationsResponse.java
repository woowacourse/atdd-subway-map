package wooteco.subway.line.dto.response;

import wooteco.subway.station.dto.StationResponse;

import java.util.List;

public class LineStationsResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineStationsResponse() {
    }

    public LineStationsResponse(LineResponse line, List<StationResponse> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations);
    }

    public LineStationsResponse(Long id, String name, String color, List<StationResponse> stations) {
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
