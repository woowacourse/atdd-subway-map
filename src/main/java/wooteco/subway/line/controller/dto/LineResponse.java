package wooteco.subway.line.controller.dto;

import java.util.List;
import wooteco.subway.line.service.dto.LineDto;
import wooteco.subway.station.controller.dto.StationResponse;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    private LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(LineDto lineDto) {
        return new LineResponse(
                lineDto.getId(),
                lineDto.getName(),
                lineDto.getColor(),
                StationResponse.ofList(lineDto.getStations()));
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
