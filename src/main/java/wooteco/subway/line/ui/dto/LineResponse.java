package wooteco.subway.line.ui.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.ui.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(final Long id, final String name, final String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stationResponses(stations);
    }

    public LineResponse(final Line line, final List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations);
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

    private List<StationResponse> stationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }
}
