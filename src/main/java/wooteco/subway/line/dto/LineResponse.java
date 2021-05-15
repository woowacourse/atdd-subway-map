package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(final Line line) {
        this(line.getId(), line.getName(), line.getName());
    }

    public LineResponse(final Line line, final List<StationResponse> stationResponse) {
        this(line.getId(), line.getName(), line.getColor(), stationResponse);
    }

    public LineResponse(final Long id, final String name, final String color) {
        this(id, name, color, Collections.EMPTY_LIST);
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
