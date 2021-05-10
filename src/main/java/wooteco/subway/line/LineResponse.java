package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.station.StationResponse;

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

    public static LineResponse from(Line line) {
        final Long id = line.getId();
        final String name = line.getName();
        final String color = line.getColor();
        final List<StationResponse> stations = line.getStations().toStream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
        return new LineResponse(id, name, color, stations);
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
