package wooteco.subway.service.dto;

import wooteco.subway.controller.response.StationResponse;

import java.util.List;

public class LineWithStationsDto {
    private Long id;
    private String color;
    private String name;
    private List<StationResponse> stations;

    public LineWithStationsDto() {
    }

    public LineWithStationsDto(LineDto lineDto, List<StationResponse> stations) {
        this(lineDto.getId(), lineDto.getColor(), lineDto.getName(), stations);
    }

    public LineWithStationsDto(Long id, String color, String name, List<StationResponse> stations) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
