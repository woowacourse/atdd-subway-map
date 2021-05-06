package wooteco.subway.line.api.dto;

import org.springframework.lang.NonNull;
import wooteco.subway.line.model.Line;
import wooteco.subway.station.api.dto.StationResponse;

import java.util.List;

public class LineResponse {

    private Long id;

    @NonNull
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

    public LineResponse(Line newLine) {
        this.id = newLine.getId();
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }

    public LineResponse(String name, String color) {
        this.name = name;
        this.color = color;
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
