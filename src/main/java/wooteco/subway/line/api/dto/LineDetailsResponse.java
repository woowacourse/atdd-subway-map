package wooteco.subway.line.api.dto;

import org.springframework.lang.NonNull;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.api.dto.StationResponse;

import java.util.List;

public class LineDetailsResponse {

    private Long id;

    @NonNull
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineDetailsResponse() {
    }

    public LineDetailsResponse(Line newLine, List<StationResponse> stations) {
        this(newLine.getId(), newLine.getName(), newLine.getColor(), stations);
    }

    public LineDetailsResponse(Long id, String name, String color, List<StationResponse> stations) {
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
