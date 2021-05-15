package wooteco.subway.controller.dto.response;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.service.dto.ReadLineDto;

public class LineResponse {

    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    @NotBlank
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public LineResponse(Long id, String name, String color,
        List<StationResponse> stations) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(LineServiceDto lineServiceDto) {
        return new LineResponse(lineServiceDto.getId(), lineServiceDto.getName(),
            lineServiceDto.getColor());
    }

    public static LineResponse from(ReadLineDto dto) {
        return new LineResponse(dto.getId(), dto.getName(), dto.getColor(),
            dto.getStationsResponses());
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
