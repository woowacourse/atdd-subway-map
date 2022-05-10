package wooteco.subway.dto;

import java.util.List;
import wooteco.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stationResponses;

    private LineResponse() {
    }

    public LineResponse(final Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
    }

    public LineResponse(final Line line, final List<StationResponse> stationResponses) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stationResponses = List.copyOf(stationResponses);
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

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
