package wooteco.subway.controller.dto.response;

import java.util.List;
import javax.validation.constraints.NotEmpty;

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
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(final Long id, final String name, final String color,
        final List<StationResponse> stations) {

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
