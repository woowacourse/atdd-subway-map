package wooteco.subway.dto;

import java.util.Collections;
import java.util.List;
import wooteco.subway.domain.Line;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = Collections.emptyList();
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
