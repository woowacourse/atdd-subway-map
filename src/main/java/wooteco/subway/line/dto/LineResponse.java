package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {

    }

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse toDto(final Line line) {
        List<Station> stations = line.getAllStations();
        List<StationResponse> stationResponses = StationResponse.toDtos(stations);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public static List<LineResponse> toDtos(final List<Line> lines) {
        return lines.stream()
                .map(LineResponse::toDto)
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
