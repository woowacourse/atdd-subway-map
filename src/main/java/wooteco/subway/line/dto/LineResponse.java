package wooteco.subway.line.dto;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.line.domain.Line;
import wooteco.subway.station.dto.StationResponse;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(Line line, List<StationResponse> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations);
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
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
}
