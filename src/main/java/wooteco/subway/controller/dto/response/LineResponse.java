package wooteco.subway.controller.dto.response;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.service.dto.ReadLineDto;

public class LineResponse {

    @NotEmpty
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(final Long id, final String name, final String color) {
        this(id, name, color, null);
    }

    public LineResponse(final Long id, final String name, final String color,
        final List<StationResponse> stations) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(final LineServiceDto lineServiceDto) {
        return new LineResponse(lineServiceDto.getId(), lineServiceDto.getName(), lineServiceDto.getColor());
    }

    public static LineResponse from(ReadLineDto dto) {
        return new LineResponse(dto.getId(), dto.getName(), dto.getColor(), dto.getStationsResponses());
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
