package wooteco.subway.service.dto;

import java.util.List;

public class LineServiceResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationServiceResponse> stations;

    public LineServiceResponse() {
    }

    public LineServiceResponse(Long id, String name, String color,
        List<StationServiceResponse> stations) {
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

    public List<StationServiceResponse> getStations() {
        return stations;
    }
}
